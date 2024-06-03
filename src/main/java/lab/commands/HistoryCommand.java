package lab.commands;
import lab.console.CommandCatalog;

public class HistoryCommand implements Command{
    /**
     * A field that refers to an object with implementations of all commands
     */
    CommandCatalog commandCatalog;
    public HistoryCommand(CommandCatalog commandCatalog) {
        this.commandCatalog = commandCatalog;
    }
    /**
     * The command that calls the required method from {@link CommandCatalog}
     */
    @Override
    public void execute(){
        commandCatalog.history();
    }
    /**
     * Method that returns command description
     * @return Command description
     */
    @Override
    public String description() {
        return "history: вывести последние 13 команд (без их аргументов)";
    }
    @Override
    public String toString() {
        return "history";
    }
}

