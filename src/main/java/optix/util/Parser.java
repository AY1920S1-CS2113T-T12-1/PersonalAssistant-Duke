package optix.util;

import optix.commands.parser.AddAliasCommand;
import optix.commands.parser.RemoveAliasCommand;
import optix.commands.parser.ResetAliasCommand;
import optix.commands.shows.AddCommand;
import optix.commands.ByeCommand;
import optix.commands.Command;
import optix.commands.shows.DeleteAllCommand;
import optix.commands.shows.DeleteOneCommand;
import optix.commands.HelpCommand;
import optix.commands.shows.EditCommand;
import optix.commands.shows.ListCommand;
import optix.commands.shows.ListDateCommand;
import optix.commands.shows.ListShowCommand;
import optix.commands.shows.PostponeCommand;
import optix.commands.seats.SellSeatCommand;
import optix.commands.seats.ViewSeatsCommand;
import optix.exceptions.OptixException;
import optix.exceptions.OptixInvalidCommandException;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse input arguments and create a new Command Object.
 */
public class Parser {

    private static final HashMap<String, String> commandAliasMap = new HashMap<>();
    // array of all possible command values
    private static String[] commandList = {"bye", "list", "help", "edit", "sell", "view",
            "postpone", "add", "delete-all", "delete"};

    /**
     * Parse input argument and create a new Command Object based on the first input word.
     *
     * @param fullCommand The entire input argument.
     * @return Command Object based on the first input word.
     * @throws OptixException if the Command word is not recognised by Optix.
     */
    public static Command parse(String fullCommand) throws OptixException, IOException {
        // read the preferences from saved file and put them into commandAliasMap
        try {
            loadPreferences();
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
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
            default:
                throw new OptixInvalidCommandException();
            }
        } else if (splitStr.length == 2) {

            // There will definitely be exceptions thrown here. Need to stress test and then categorise
            switch (commandName) {

            case "edit":
                return parseEditShow(splitStr[1]);
            case "sell":
                return parseSellSeats(splitStr[1]);
            case "view":
                return parseViewSeating(splitStr[1]);
            case "postpone":
                return parsePostpone(splitStr[1]);
            case "list":
                return parseList(splitStr[1]);
            case "bye":
                return new ByeCommand();
            case "add": // add poto|5/10/2020|2000|20
                return parseAddShow(splitStr[1]);
            case "delete-all": // e.g. delete-all poto|lion king
                return parseDeleteAllOfShow(splitStr[1]);
            case "delete": // e.g. delete 2/10/2019|poto
                return parseDeleteOneOfShow(splitStr[1]);
            case "help":
                return new HelpCommand(splitStr[1].trim());
            case "add-alias":
                return parseAddAlias(splitStr[1]);
            case "remove-alias":
                return parseRemoveAlias(splitStr[1]);
            default:
                throw new OptixInvalidCommandException();
            }
        } else {
            throw new OptixInvalidCommandException();
        }
    }

    private static Command parseRemoveAlias(String splitStr) throws OptixException {
        String[] aliasDetails = splitStr.split("\\|",2);
        String alias = aliasDetails[0];
        String command = aliasDetails[1];
        if (commandAliasMap.containsValue(command) && commandAliasMap.containsKey(alias)) {
            return new RemoveAliasCommand(alias, command, commandAliasMap);
        } else {
            throw new OptixException("Error removing alias.\n");
        }
    }

    private static Command parseAddAlias(String splitStr) throws OptixException {
        String[] aliasDetails = splitStr.split("\\|",2);
        String alias = aliasDetails[0];
        String command = aliasDetails[1];
        if (commandAliasMap.containsValue(command) && !commandAliasMap.containsKey(alias)) {
            return new AddAliasCommand(alias, command, commandAliasMap);
        } else {
            throw new OptixException("Alias already exists, or the command to alias does not exist.\n");
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
        commandAliasMap.put("D", "delete-all");
        commandAliasMap.put("d", "delete");
    }

    /**
     * Parse the remaining user input to its respective parameters for PostponeCommand.
     *
     * @param postponeDetails The details to create new PostponeCommand Object.
     * @return new PostponeCommand Object.
     * @throws OptixInvalidCommandException if the user input does not have the correct number of parameters.
     */
    private static Command parsePostpone(String postponeDetails) throws OptixInvalidCommandException {
        String[] splitStr = postponeDetails.trim().split("\\|", 3);

        if (splitStr.length != 3) {
            throw new OptixInvalidCommandException();
        }

        String showName = splitStr[0].trim();
        String oldDate = splitStr[1].trim();
        String newDate = splitStr[2].trim();

        return new PostponeCommand(showName, oldDate, newDate);
    }

    /**
     * Parse the remaining user input to its respective parameters for AddCommand.
     *
     * @param showDetails The details to create a new AddCommand Object.
     * @return new AddCommand Object.
     * @throws OptixInvalidCommandException if the user input does not have the correct number of parameters.
     * @throws NumberFormatException        if user attempt to convert String into double.
     */
    private static Command parseAddShow(String showDetails) throws OptixInvalidCommandException, NumberFormatException {
        String[] splitStr = showDetails.trim().split("\\|", 4);

        if (splitStr.length != 4) {
            throw new OptixInvalidCommandException();
        }

        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();
        double showCost = Double.parseDouble(splitStr[2]);
        double seatBasePrice = Double.parseDouble(splitStr[3]);

        return new AddCommand(showName, showDate, showCost, seatBasePrice);
    }

    /**
     * Parse the remaining user input to its respective parameters for DeleteOneCommand.
     *
     * @param showDetails The details to create a new DeleteOneCommand Object.
     * @return new DeleteOneCommand Object.
     * @throws OptixInvalidCommandException if the user input does not have the correct number of parameters.
     */
    private static Command parseDeleteOneOfShow(String showDetails) throws OptixInvalidCommandException {
        String[] splitStr = showDetails.trim().split("\\|");

        if (splitStr.length != 2) {
            throw new OptixInvalidCommandException();
        }

        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();

        return new DeleteOneCommand(showName, showDate);
    }

    /**
     * Parse the remaining user input to its respective parameters for DeleteAllCommand.
     *
     * @param deleteDetails The name of all the shows being queried.
     * @return new DeleteAllCommand Object.
     */
    private static Command parseDeleteAllOfShow(String deleteDetails) {
        String[] splitStr = deleteDetails.trim().split("\\|");

        return new DeleteAllCommand(splitStr);
    }

    /**
     * Parse the remaining user input to its respective parameters for ViewSeatsCommand.
     *
     * @param showDetails The details to create a new ViewSeatsCommand Object.
     * @return new ViewSeatsCommand Object.
     * @throws OptixInvalidCommandException if the user input does not have the correct number of parameters.
     */
    private static Command parseViewSeating(String showDetails) throws OptixInvalidCommandException {
        String[] splitStr = showDetails.trim().split("\\|");

        if (splitStr.length != 2) {
            throw new OptixInvalidCommandException();
        }

        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();

        return new ViewSeatsCommand(showName, showDate);
    }

    /**
     * Parse the remaining user input to its respective parameters for SellSeatsCommand.
     *
     * @param details The details to create a new SellSeatsCommand Object.
     * @return new SellSeatsCommand Object.
     * @throws OptixInvalidCommandException if the user input does not have the correct number of parameters.
     */
    private static Command parseSellSeats(String details) throws OptixInvalidCommandException {
        String[] splitStr = details.trim().split("\\|");

        if (splitStr.length < 3 || splitStr.length > 4) {
            throw new OptixInvalidCommandException();
        }

        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();
        String buyerName = splitStr[2].trim();

        if (splitStr.length == 4) {
            String seats = splitStr[3].trim();

            return new SellSeatCommand(showName, showDate, buyerName, seats);
        }

        return new SellSeatCommand(showName, showDate, buyerName);

    }

    /**
     * Parse the remaining user input to its respective parameters for EditCommand.
     *
     * @param details The details to create a new EditCommand Object.
     * @return new EditCommand Object.
     * @throws OptixInvalidCommandException if the user input does not have the correct number of parameters.
     */
    private static Command parseEditShow(String details) throws OptixInvalidCommandException {
        String[] splitStr = details.split("\\|");

        if (splitStr.length != 3) {
            throw new OptixInvalidCommandException();
        }

        String oldShowName = splitStr[0].trim();
        String showDate = splitStr[1].trim();
        String newShowName = splitStr[2].trim();

        return new EditCommand(oldShowName, showDate, newShowName);
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
