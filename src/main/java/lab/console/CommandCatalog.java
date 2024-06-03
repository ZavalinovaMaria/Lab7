package lab.console;

import lab.client.OnlineUser;
import lab.commands.Command;
import lab.dataBaseTools.DatabaseProvider;
import lab.tools.Inserting;
import lab.tools.TicketCollection;
import lab.tools.exceptions.NotExistingValueException;
import lab.tools.exceptions.NotUniqueValueException;
import lab.tools.subjects.Ticket;
import lab.tools.subjects.comporators.ComparatorDiscount;

import java.io.File;
import java.util.*;

import static lab.console.Console.*;

public class CommandCatalog {
    private final Inserting insert;
   // private final DatabaseWriter writer;
    private final DatabaseProvider provider;
    private final OnlineUser user;


    private TicketCollection collection;
    /**
     * A field that refers to an object whose fields contain the collection with which the program works.
     */
    private Set<String> scriptHistory = new HashSet<>();
    /**
     * A field that contains history of executed commands
     */
    private final List<String> commandHistory = new ArrayList<>();
    /**
     * An array of strings into which an array of commands from the script is passed{@link ScriptManager#executeScript()}
     */

    private String[] compositeCommand = new String[9];
    /**
     * A field that can be used to change the implementation of commands for working with a script
     *
     * @see CommandCatalog#executeScript()
     */
    private boolean isScriptWorking = false;
    /**
     * String array to which commands are sent from the console
     */
    private String[] tokens;

    public CommandCatalog(TicketCollection collection, DatabaseProvider provider, Inserting insert, Map<String, Command> commands, OnlineUser user) {
        this.collection = collection;
        // this.reader = reader;
        //this.writer = writer;
        this.provider = provider;
        this.insert = insert;
        this.user =user;
    }

    public void addToHistory(String command) {
        commandHistory.add(command);
    }
    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }
    public void clearCompositeCommand() {
        compositeCommand = new String[9];
    }
    public void setCompositeCommand(String[] compositeCommand) {
        this.compositeCommand = compositeCommand;
    }

    /**

     *
     * @see TicketCollection
     */
    public void info() {
        System.out.print("Информация о коллекции:");
        System.out.println(collection.toString());
    }

    /**

     */
    public void help() {
        for (Command com : commands.values()) {
            System.out.println(com.description());
        }
    }

    /**

     */
    public void show() {
        System.out.println("Коллекция: ");
        System.out.println(collection.getCollection());

    }
    /**

     */
    public void exit() {
        System.out.println("Конец текущей сессии");
        System.exit(0);
    }

    /**
     *
     */
    public void clear() {
        if(collection.getUsersTickets(user)!=null) {
            collection.getUsersTickets(user).clear();
            for (Ticket ticket : collection.getUsersTickets(user).values()) {
                provider.removeElementFromBase(ticket);
            }
            collection.updateData();
            System.out.println("Коллекция изменена ");
        }
        else System.out.println("Элементов, созданных вами нет в коллекции");}

    /**

     */
    public void history() {
        if (commandHistory.isEmpty()) {
            System.out.println("История команд пуста");
            return;
        }
        for (String command : commandHistory.subList(Math.max(commandHistory.size() - 13, 0), commandHistory.size())) {
            System.out.println(command);
        }
    }


    /**
     * A command that creates an object of the {@link ScriptManager} class and starts the script process
     *

     */
    public void executeScript() {
        try {
            File script;
            if (isScriptWorking) {
                script = new File(compositeCommand[0]);
            } else {
                script = new File(tokens[1]);
            }
            if (!script.exists()) {
                System.out.println("Указанный файл не существует");
                return;
            }
            if (scriptHistory.contains(compositeCommand[0]) ) {
                System.out.println("Данный скрипт уже был выполнен");
                return;
            }
            if (isScriptWorking) {
                scriptHistory.add(compositeCommand[0]);
            } else {
                scriptHistory.add(tokens[1]);
            }

            isScriptWorking = true;
            ScriptManager scriptManager = new ScriptManager(script, commands, this);
            System.out.println("Выполнение скрипта");
            if (isScriptWorking) {
                clearCompositeCommand();
            }
            scriptManager.executeScript();
            isScriptWorking = false;
            System.out.println("Выполнение скрипта завершилось успешно");

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Пожалуйста, введите имя файла");
        }
    }

    /**

     */
    public void printFieldDescendingDiscount() {
        List<Ticket> sortable = new ArrayList<>(collection.getCollection().values());
        Comparator<Ticket> sortability = new ComparatorDiscount();
        sortable.sort(sortability);
        Collections.reverse(sortable);
        System.out.println("Вывод значения поля discount всех элементов в порядке убывания: ");
        for (Ticket ticket : sortable) {
            System.out.println(ticket.getDiscount());
        }
    }

    /**

     */
    public void removeLowerKey() {
        boolean flag=false;
        try {
            Integer key;
            if (isScriptWorking) {
                key = Integer.parseInt(compositeCommand[0]);
                clearCompositeCommand();
            } else {
                key = Integer.parseInt(tokens[1]);
            }
            Iterator<Ticket> iterator = collection.getCollection().values().iterator();
            while (iterator.hasNext()) {
                Ticket ticket = iterator.next();
                if (ticket.getId() < key && provider.canModifyProduct(ticket.getId()) ) {
                    collection.deleteKey(ticket.getId());
                    iterator.remove();
                    if(provider.removeElementFromBase(ticket)){
                        flag = true;}
                }
            }
            collection.updateData();

        } catch (NumberFormatException e) {
            System.out.println("Ключ дожен быть числом, введите команду еще раз ");
        }
        if(flag) System.out.println("Элементы из коллекции успешно удалены");
        else System.out.println("Коллекция осталась без изменений ");

    }

    /**

     */
    public void removeLower() {
        try {
            Integer key;
            if (isScriptWorking) {
                key = Integer.parseInt(compositeCommand[0]);
                clearCompositeCommand();
            } else {
                key = Integer.parseInt(tokens[1]);
            }
            if (collection.checkingExistence(key)) {
                Iterator<Ticket> iterator = collection.getCollection().values().iterator();
                while (iterator.hasNext()) {
                    Ticket ticket = iterator.next();
                    if (ticket.getPrice() < collection.getCollection().get(key).getPrice() && provider.canModifyProduct(ticket.getId())){
                        iterator.remove();
                        collection.deleteKey(ticket.getId());
                        if(provider.removeElementFromBase(ticket)){
                            System.out.println("Элемет c ключом "+ticket.getId()+ " успешно удален ");}
                    }
                }
                collection.updateData();
            }else{
                System.out.println("Коллекция осталась без изменений ");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ключ должен быть числом ");
        } catch (NotExistingValueException e) {
            System.out.println("Элемент с таким ключом не содержится в коллекции и  не может быть удален ");
        }
    }


    /**

     */
    public void save() {
      //  writer.writeToFile(collection, firstFilePath);
    }

    /**

     */
    public void insert() {
        boolean flag = false;
        if (isScriptWorking) {
            try {
                Ticket newTicket = insert.toBuildTicket(compositeCommand);
                if(provider.addElementToBase(newTicket)) {
                    if (insert.checkingUniqueness(newTicket.getId()) && insert.checkingIdUniqueness(newTicket.getVenue().getId())) {
                        collection.getCollection().put(newTicket.getId(), newTicket);
                        keyStoragee.add(newTicket.getId());
                        idVenueStorage.add(newTicket.getVenue().getId());
                        clearCompositeCommand();
                        flag = true;
                    }
                }
            } catch (NumberFormatException E) {
                System.out.println("Ошибка заполнения полей ");
            } catch (NotUniqueValueException e) {
                System.out.println(e.getMessage());
            }

        } else {
            Ticket newTicket = insert.createTicket();
            if(provider.addElementToBase(newTicket)){//муторно как то на уникальность sql не надеюсь
                collection.getCollection().put(newTicket.getId(), newTicket);
                flag = true;}
        }
        collection.updateData();
        if(flag) System.out.println("Новый элемент добавлен в коллекцию");
        else System.out.println("Не удалось добавить элемент в коллекцию");
    }

    /**
     *
     */
    public void removeKey() {
        boolean flag = false;
        try {
            Integer key;
            if (isScriptWorking) {
                key = Integer.parseInt(compositeCommand[0]);
                clearCompositeCommand();
            } else {
                key = Integer.parseInt(tokens[1]);
            }
            if(provider.canModifyProduct(key) && collection.getCollection().containsKey(key)){
                collection.deleteKey(key);///
                collection.getCollection().remove(key);
                collection.updateData();
                if(provider.removeElementFromBase(collection.getCollection().get(key))){
                    flag=true;}
            }} catch (NumberFormatException e) {
            System.out.println("Ключ должен быть числом ");
        }
        if (flag) System.out.println("Элемент успешно удален из коллекции");
        else System.out.println("Не удалось удалить элемент ");
    }

    /**

     */
    public void updateId() {
        boolean flag = false;
        try {
            Integer id;
            if (isScriptWorking) {
                id = Integer.parseInt(compositeCommand[0]);
                if (collection.checkingExistence(id) && provider.canModifyProduct(collection.getCollection().get(id).getId()) ) {
                    Integer oldId = collection.getCollection().get(id).getVenue().getId();
                    if(provider.updateElementInBase(id,insert.toBuildUpdationTicket(collection.getCollection().get(id), compositeCommand))){
                        idVenueStorage.add(collection.getCollection().get(id).getVenue().getId());
                        idVenueStorage.remove(oldId);
                        clearCompositeCommand();
                        flag = true;
                    }
                }
            } else {
                id = Integer.parseInt(tokens[1]);
                if (collection.checkingExistence(id) && provider.canModifyProduct(collection.getCollection().get(id).getId())) {
                    Integer oldId = collection.getCollection().get(id).getVenue().getId();
                    if(provider.updateElementInBase(id,insert.toBuildUpdationTicket(collection.getCollection().get(id), compositeCommand))){
                        idVenueStorage.add(collection.getCollection().get(id).getVenue().getId());
                        idVenueStorage.remove(oldId);
                        flag = true;
                    }}
            }
        } catch (NumberFormatException e) {
            System.out.println("Id должен быть числом ");
        } catch (NotExistingValueException e) {
            System.out.println("Элемент с подобным ключем не обнаружен ");
        }
        if (flag)System.out.println("Значение элемента успешно обновлено");
        else System.out.println("Не удалось обновить значения элемента ");
    }

    /**

     */
    public void sumOfPrice() {
        double totalPrice = 0;
        Collection<Ticket> values = collection.getCollection().values();
        for (Ticket ticket : values) {
            totalPrice += ticket.getPrice();
        }
        System.out.println("Сумма значений поля price для всех элементов коллекции: " + totalPrice);
    }


    /**

     */
    public void filterContainsName() {
        String input;
        if (isScriptWorking) {
            input = compositeCommand[0];
            clearCompositeCommand();
        } else {
            input = tokens[1];
        }
        ArrayList<Ticket> elements = new ArrayList<>();
        for (Ticket ticket : collection.getCollection().values()) {
            String value = ticket.getName();
            if (value.contains(input)) {
                elements.add(ticket);
            }
        }
        if (elements.isEmpty()) {
            System.out.println("В коллекции нет элементов, содержащих данную подстроку");
        } else {
            System.out.println("Данную подстроку в имени имеют следущие элементы:");
            for (Ticket element : elements) {
                System.out.println(element);
            }
        }
    }
}





