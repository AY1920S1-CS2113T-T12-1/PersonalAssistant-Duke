package optix.util;

import optix.commands.ByeCommand;
import optix.commands.Command;
import optix.commands.HelpCommand;
import optix.commands.parser.AddAliasCommand;
import optix.commands.parser.ListAliasCommand;
import optix.commands.parser.RemoveAliasCommand;
import optix.commands.parser.ResetAliasCommand;
import optix.commands.seats.SellSeatCommand;
import optix.commands.seats.ViewSeatsCommand;
import optix.commands.shows.AddCommand;
import optix.commands.shows.DeleteCommand;
import optix.commands.shows.EditCommand;
import optix.commands.shows.ListCommand;
import optix.commands.shows.ListDateCommand;
import optix.commands.shows.ListShowCommand;
import optix.commands.shows.PostponeCommand;
import optix.commands.shows.ViewMonthlyCommand;
import optix.commands.shows.ViewProfitCommand;
import optix.exceptions.OptixException;
import optix.exceptions.OptixInvalidCommandException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse input arguments and create a new Command Object.
 */
public class Parser {

    private static HashMap<String, String> commandAliasMap;

    // array of all possible command values
    private static String[] commandList = {"bye", "list", "help", "edit", "sell", "view",
        "postpone", "add", "delete"};


    /**
     * Parse input argument and create a new Command Object based on the first input word.
     *
     * @param fullCommand The entire input argument.
     * @return Command Object based on the first input word.
     * @throws OptixException if the Command word is not recognised by Optix.
     */
    public static Command parse(String fullCommand) throws OptixException {
        // read the preferences from saved file and put them into commandAliasMap
        try {
            loadPreferences();
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
        // populate commandAliasMap
        commandAliasMap.put("s", "sell");
        commandAliasMap.put("v", "view");
        commandAliasMap.put("a", "add");
        commandAliasMap.put("d", "delete");
        commandAliasMap.put("e", "edit");
        commandAliasMap.put("L", "list");
        commandAliasMap.put("p", "postpone");
        commandAliasMap.put("b", "bye");
        commandAliasMap.put("h", "help");


        // add exception for null pointer exception. e.g. postpone
        String[] splitStr = fullCommand.trim().split(" ", 2);
        String aliasName = splitStr[0];
        String commandName = commandAliasMap.getOrDefault(aliasName, aliasName);
        commandName = commandName.toLowerCase().trim(); // is the lower case and trim necessary ?

        if (splitStr.length == 1) {
            switch (commandName) {
            case "bye":
                return new ByeCommand();
            case "list":
                return new ListCommand();
            case "help":
                return new HelpCommand();
            case "reset-alias":
                return new ResetAliasCommand();
            case "list-alias":
                return new ListAliasCommand();
            default:
                throw new OptixInvalidCommandException();
            }
        } else if (splitStr.length == 2) {

            // There will definitely be exceptions thrown here. Need to stress test and then categorise
            switch (commandName) {

            case "edit":
                return new EditCommand(splitStr[1]);
            case "sell":
                return new SellSeatCommand(splitStr[1]);
            case "view":
                return new ViewSeatsCommand(splitStr[1]);
            case "postpone":
                return new PostponeCommand(splitStr[1]);
            case "list":
                return parseList(splitStr[1]);
            case "bye":
                return new ByeCommand();
            case "add": // add poto|5/10/2020|20
                return new AddCommand(splitStr[1]);
            case "delete": // e.g. delete SHOW_NAME DATE_1|DATE_2|etc
                return new DeleteCommand(splitStr[1]);
            case "view-profit": //e.g. view-profit lion king|5/5/2020
                return new ViewProfitCommand(splitStr[1]);
            case "view-monthly": //e.g. view-monthly May 2020
                return new ViewMonthlyCommand(splitStr[1]);
            case "help":
                return new HelpCommand(splitStr[1].trim());
            case "add-alias":
                return new AddAliasCommand(splitStr[1]);
            case "remove-alias":
                return new RemoveAliasCommand(splitStr[1], commandAliasMap);
            default:
                throw new OptixInvalidCommandException();
            }
        } else {
            throw new OptixInvalidCommandException();
        }
    }

    public void addAlias(String newAlias, String command) throws OptixException {
        if (Arrays.asList(commandList).contains(command) && !commandAliasMap.containsKey(newAlias)) {
            commandAliasMap.put(newAlias, command);
        } else {
            throw new OptixException("Alias is already in use, or command does not exist.");
        }
    }

    private static void loadPreferences() throws IOException {
        File currentDir = new File(System.getProperty("user.dir"));
        File filePath = new File(currentDir.toString() + "\\src\\main\\data\\ParserPreferences.txt");
        if (filePath.exists() && filePath.length() > 0) {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String aliasPreference;
            while ((aliasPreference = br.readLine()) != null) {
                String[] aliasDetails = aliasPreference.split("\\|");
                String alias = aliasDetails[0];
                String command = aliasDetails[1];
                if (Arrays.asList(commandList).contains(command)) {
                    commandAliasMap.put(alias, command);
                } else {
                    System.out.println("error inserting alias preference.");
                }

            }
            br.close();
            fr.close();
        } else {
            resetPreferences();
            savePreferences();
        }
    }

    private static void savePreferences()  {
        File currentDir = new File(System.getProperty("user.dir"));
        File filePath = new File(currentDir.toString() + "\\src\\main\\data\\ParserPreferences.txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        for (Map.Entry<String, String> entry : commandAliasMap.entrySet()) {
            assert writer != null;
            writer.println(entry.getKey() + "\\|" + entry.getValue());
        }
        assert writer != null;
        writer.close();
    }

    private static void resetPreferences() {
        commandAliasMap.clear();
        commandAliasMap.put("b", "bye");
        commandAliasMap.put("l", "list");
        commandAliasMap.put("h", "help");
        commandAliasMap.put("e", "edit");
        commandAliasMap.put("s", "sell");
        commandAliasMap.put("v", "view");
        commandAliasMap.put("p", "postpone");
        commandAliasMap.put("a", "add");
        commandAliasMap.put("d", "delete");
    }


    /**
     * Parse the remaining user input to its respective parameters for ListDateCommand or ListShowCommand.
     *
     * @param details The details to create a new ListDateCommand or ListShowCommand Object.
     * @return new ListDateCommand or ListShowCommand Object.
     */
    private static Command parseList(String details) {
        String[] splitStr = details.split(" ");

        if (splitStr.length == 2) {
            try {
                Integer.parseInt(splitStr[1]);
                return new ListDateCommand(details);
            } catch (NumberFormatException e) {
                return new ListShowCommand(details);
            }
        }

        return new ListShowCommand(details);
    }
}
