/**
 * Represents a person with their first and last name
 */

abstract class Person {
    String firstName;
    String lastName;

    /**
     * Constructs a new Person with the specified attributes.
     *
     * @param firstName  The first name of the person.
     * @param lastName   The last name of the person.
     */
    Person(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets a person's full name
     *
     * @return The person's full name
     * **/
    public String getFullName(){
        return this.firstName + ' ' + this.lastName;
    }
}