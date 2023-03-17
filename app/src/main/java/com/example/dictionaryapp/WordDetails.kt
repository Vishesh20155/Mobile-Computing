package com.example.dictionaryapp

data class WordDetails(var partOfSpeech: String = "",
                       var definitions: MutableList<DefinitionContents> = mutableListOf()
)
