abstract class Account {
    String name;

    Account(String name){
        this.name = name;
    }

    // put money into account
    abstract void deposit(double amount);

    // take money out, return if success
    abstract boolean withdraw(double amount);
}
