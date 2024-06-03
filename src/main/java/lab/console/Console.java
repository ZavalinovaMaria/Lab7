package lab.console;

import lab.client.OnlineUser;
import lab.commands.Command;
import lab.commands.*;
import lab.dataBaseTools.DatabaseProvider;
import lab.dataBaseTools.DatabaseReader;
import lab.tools.Inserting;
import lab.tools.TicketCollection;

import java.util.*;

/**
 * Class in which all objects necessary for work are created and the program starts working
 */
public class Console {

    static Map<String, Command> commands = new HashMap<>();
    public static ArrayList<Integer> keyStoragee = new ArrayList<>();
    public static ArrayList<Integer> idVenueStorage = new ArrayList<>();
    public final OnlineUser user;
    public static String firstFilePath;
    public Console(OnlineUser user){
        this.user = user;
    }

    /**
     * The starting point of the program. A method in which a database file is specified, commands are specified, and commands are entered.
     */
    public void toStart() {
        /*
        String path;
        try {
            firstFilePath = filepath;
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println("Файл с указанной директорией не обнаружен");
                return;
            }
        } catch (NoSuchElementException e) {
            System.out.println("Пустая строка, выход из программы");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Не указан путь к файлу ");
            return;
        }
        if (filepath.isEmpty()) {
            System.out.println("Передан пустой файл");
            return;
        }

         */

        DatabaseReader reader = new DatabaseReader();
        //DatabaseWriter writer = new DatabaseWriter();
        DatabaseProvider provider = new DatabaseProvider(user);
        Inserting insert = new Inserting();
        keyStoragee = new ArrayList<>();
        idVenueStorage = new ArrayList<>();
        TicketCollection collection = new TicketCollection(reader.read());
        CommandCatalog commandCatalog = new CommandCatalog(collection,provider, insert, commands,user);

        commands.put("help", new HelpCommand(commandCatalog));
        commands.put("save", new SaveCommand(commandCatalog));
        commands.put("clear", new ClearCommand(commandCatalog));
        commands.put("show", new ShowCommand(commandCatalog));
        commands.put("exit", new ExitCommand(commandCatalog));
        commands.put("update_id", new UpdateIdCommand(commandCatalog));
        commands.put("info", new InfoCommand(commandCatalog));
        commands.put("insert", new InsertCommand(commandCatalog));
        commands.put("remove_key", new RemoveKeyCommand(commandCatalog));
        commands.put("execute_script", new ExecuteScriptCommand(commandCatalog));
        commands.put("remove_lower", new RemoveLowerCommand(commandCatalog));
        commands.put("remove_lower_key", new RemoveLowerKeyCommand(commandCatalog));
        commands.put("history", new HistoryCommand(commandCatalog));
        commands.put("filter_contains_name", new FilterContainsNameCommand(commandCatalog));
        commands.put("sum_of_price", new SumOfPriceCommand(commandCatalog));
        commands.put("print_field_descending_discount", new PrintFieldDescendingDiscountCommand(commandCatalog));


        while (true) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Введите команду");
            String query = null;
            try {
                query = scan.nextLine().toLowerCase();//}

                // catch (NullPointerException e) {
                //  System.out.println("Введенная команда не существует");
                // } catch (ArrayIndexOutOfBoundsException e) {
                // System.out.println("Недопустимый формат команды");

                String[] tokens = query.split(" \\s+");
                if (tokens.length == 0) {
                    tokens = query.split("\n");
                }
                commandCatalog.setTokens(tokens);
                Command command = commands.get(tokens[0]);
                try {
                    command.execute();
                    commandCatalog.addToHistory(String.valueOf(command));

                } catch (NullPointerException ev) {
                    System.out.println("Введенная команда не существует");
                } catch (ArrayIndexOutOfBoundsException ev) {
                    System.out.println("Недопустимый формат команды");
                } catch (NoSuchElementException ev) {
                    System.out.println("Пустая строка, выход из программы");
                }

            } catch (NullPointerException ev) {
                System.out.println("Введенная команда не существует");
            } catch (ArrayIndexOutOfBoundsException ev) {
                System.out.println("Недопустимый формат команды");
            } catch (NoSuchElementException ev) {
                System.out.println("Пустая строка, выход из программы");
                scan.close();
            }
        }




    }
}



