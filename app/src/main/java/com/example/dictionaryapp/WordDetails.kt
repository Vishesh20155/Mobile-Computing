package com.example.dictionaryapp

data class WordDetails(var partOfSpeech: String = "",
                       var definitions: MutableList<DefinitionContents> = mutableListOf(),
                       var synonyms: MutableList<String> = mutableListOf(),
                       var antonyms: MutableList<String> = mutableListOf()
)
