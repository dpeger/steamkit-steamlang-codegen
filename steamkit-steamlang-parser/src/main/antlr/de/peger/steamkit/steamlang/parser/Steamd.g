grammar Steamd;

options {
  // antlr will generate java lexer and parser
  language = Java;
  // generated parser should create abstract syntax tree
  output = AST;
}

tokens {
  // imaginary tokens for the overall structure
  IMPORTS;
  ENUMS;
  CLASSES;
  
  TYPEREF;
  MODIFIERS;
  FIELDS;
  
  FIELD_MODIFIERS;
  FIELD_TYPE;
  FIELD_VALUE;
  FIELD_COMMENT;
}

@lexer::header {
  package de.peger.steamkit.steamlang.parser;
}
 
@parser::header {
  package de.peger.steamkit.steamlang.parser;
}

// LEXER rules
fragment UPPERCASELETTER: ('A' .. 'Z');
fragment LOWERCASELETTER: ('a' .. 'z');
fragment DIGIT: ('0' .. '9');
fragment IDCHAR: (UPPERCASELETTER | LOWERCASELETTER | DIGIT | '_');
fragment IDFGMNT: (UPPERCASELETTER | LOWERCASELETTER) (IDCHAR)*;

WS :  ( ' ' | '\t' | '\r' | '\n') { $channel = HIDDEN; };

COMMENT: (('//')|('obsolete'))~('\r'|'\n')*;

// keywords
IMPORT: '#import';
ENUM: 'enum';
CLASS: 'class';
PRIMITIVE: ('long'|'int'|'uint'|'short'|'ushort'|'byte');

CURBRAOP: '{';
CURBRACL: '}';
SEMCOL: ';';
ASSIGNMENT: '=';
OPERATOR: '|';
STRINGLITERAL: ('"' .* '"');
NUMBER: ('0') | (('-')?('1' .. '9') (DIGIT)*);
BYTEVAL: ('0x') (UPPERCASELETTER | LOWERCASELETTER | DIGIT)+;
IDENTIFIER: IDFGMNT ('.' IDFGMNT)*;
REFERENCE: (IDFGMNT)+ '::' (IDFGMNT)+;

// PARSER rules
import_decl: IMPORT STRINGLITERAL -> STRINGLITERAL;

enum_field_value: (NUMBER | BYTEVAL | IDENTIFIER) (OPERATOR (NUMBER | BYTEVAL | IDENTIFIER))*;
enum_field:
  IDENTIFIER ASSIGNMENT enum_field_value SEMCOL enum_field_comment=COMMENT?
  -> ^(IDENTIFIER ^(FIELD_VALUE enum_field_value) ^(FIELD_COMMENT $enum_field_comment*));

enum_decl:
  ENUM enum_name=IDENTIFIER ('<' enum_type=PRIMITIVE '>')? enum_modifier=IDENTIFIER? CURBRAOP (enum_field | COMMENT)+ CURBRACL SEMCOL?
  -> ^($enum_name ^(TYPEREF $enum_type?) ^(MODIFIERS $enum_modifier?) ^(FIELDS enum_field*));

class_field_value: (NUMBER | BYTEVAL | REFERENCE | IDENTIFIER);
class_field_modifier: IDENTIFIER('<'IDENTIFIER'>')?;
class_field_type: (PRIMITIVE|IDENTIFIER)('<'NUMBER'>')?;
class_field: class_field_modifier? class_field_type field_name=IDENTIFIER (ASSIGNMENT class_field_value)? SEMCOL COMMENT?
  ->^($field_name ^(FIELD_MODIFIERS class_field_modifier*) ^(FIELD_TYPE class_field_type*) ^(FIELD_VALUE class_field_value*) ^(FIELD_COMMENT COMMENT*));

class_decl:
  CLASS class_name=IDENTIFIER ('<' class_type_ref=REFERENCE '>')? CURBRAOP class_field* CURBRACL SEMCOL?
  -> ^($class_name ^(TYPEREF $class_type_ref*) ^(FIELDS class_field*));
    
// start rule
steamd:
  (import_decl* (enum_decl | class_decl)+ EOF)
  -> ^(IMPORTS import_decl*) ^(CLASSES class_decl*) ^(ENUMS enum_decl*);
