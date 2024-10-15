abstract class Person {
    String firstName;
    String lastName;
    int dob;

    // person constructor
    Person(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // return first and last name
    public String getFullName(){
        return this.firstName + ' ' + this.lastName;
    }
}