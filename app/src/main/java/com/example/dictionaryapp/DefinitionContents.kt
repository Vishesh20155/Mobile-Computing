package com.example.dictionaryapp

data class DefinitionContents(var definition: String = "",
                              var example: String = "",
                              var synonyms: MutableList<String> = mutableListOf(),
                              var antonyms: MutableList<String> = mutableListOf()
)
