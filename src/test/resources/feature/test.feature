Feature: Change user Info in the Profile


  Scenario:  go to the User Profile to change First Name, Last Name and PhoneNumber

    Given GeoRent  is opened in my browser
    When press button with text "Sign in"
    And type to input with name "email" text: "user1@gmail.com.ua"
    And type to input with name "password" text: "12345678"
    And press button with value "Enter"
    And click the link with value "Edit profile"
    And type to input with nameField text: "Bon"
    And type to input with lastNameField text: "Fox"
    And type to input with field text: "35"
    And press Save button
    Then press Exit button







  Scenario:  go to the User Profile to reset First Name, Last Name and PhoneNumber

    Given GeoRent  is opened in my browser
    When press button with text "Sign in"
    And type to input with name "email" text: "user1@gmail.com.ua"
    And type to input with name "password" text: "12345678"
    And press button with value "Enter"
    And click the link with value "Edit profile"
    And press Reset button
    And type to input with nameField text: "James"
    And type to input with lastNameField text: "Bond"
    And type to input with field text: "3243545"
    And press Save button
    Then press Exit button