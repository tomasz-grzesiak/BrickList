package com.example.bricklist.logic

class Project {
    private val id: Int
    private val name: String
    private val active: Boolean
    private val lastAccessed: Int

    constructor(id: Int, name: String, active: Boolean, lastAccessed: Int) {
        this.id = id
        this.name = name
        this.active = active
        this.lastAccessed = lastAccessed
    }

    fun getId(): Int {
        return this.id
    }

    fun getName(): String {
        return this.name
    }
}