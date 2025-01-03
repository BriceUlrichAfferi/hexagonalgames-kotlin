package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

/**
 * This class represents a User data object. It holds basic information about a user, including
 * their ID, first name, and last name. The class implements Serializable to allow for potential
 * serialization needs.
 */

// Default value
data class User(
  /**
   * Unique identifier for the User.
   */
  val id: String = "",
  
  /**
   * User's first name.
   */
  val firstName: String = "",
  
  /**
   * User's last name.
   */
  val lastName: String = ""
) : Serializable
