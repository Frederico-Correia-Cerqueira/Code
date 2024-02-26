package com.example.gomoku.domain




/**
 * Used to represent information about a social network in the about screen
 * @param uri the link to the social network
 * @param image the id of the image to be displayed
 */
data class Social(val uri: String, val image: String)

/**
 * Used to represent information about a developer in the about screen
 * @param name the developer's name
 * @param number the developer's number
 * @param socials a list of the developer's social networks
 * @param email the developer's email
 * @param image the id of the image to be displayed
 */
data class Developer(val name: String, val number: String, val socials: List<Social>, val email: String, val image: String)

/**
 * Used to represent the app information, such as the version and the developers
 * @param version the app version
 * @param developers the app developers
 */
data class Information(val version: String, val developers:List<Developer>)