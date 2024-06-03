package lab.commands;

public interface Command {
    /**
     * Command execution method
     */
    void execute();
    /**
     * Method that return description
     * @return Command description
     */
    String description();
}
