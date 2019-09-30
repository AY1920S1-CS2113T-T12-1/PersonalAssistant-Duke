package optix.parser;

import optix.commands.AddCommand;
import optix.commands.ByeCommand;
import optix.commands.Command;
import optix.commands.DeleteAllCommand;
import optix.commands.DeleteOneCommand;
import optix.commands.ListCommand;
import optix.commands.ListShowCommand;
import optix.commands.PostponeCommand;
import optix.commands.SellSeatCommand;
import optix.commands.ViewSeatsCommand;

public class Parser {
    public static Command parse(String fullCommand) {
        // add exception for null pointer exception. e.g. postpone
        String[] splitStr = fullCommand.trim().split(" ", 2);
      
        if (splitStr.length == 1) {
            switch (splitStr[0].toLowerCase()) {
            case "bye":
                return new ByeCommand();
            case "list":
                return new ListCommand();
            default:
                return null;
            }
        } else {
            // There will definitely be exceptions thrown here. Need to stress test and then categorise
            switch (splitStr[0].toLowerCase()) {
            case "sell":
                return parseSellSeats(splitStr[1]);
            case "view":
                return parseViewSeating(splitStr[1]);
            case "postpone":
                return parsePostpone(splitStr[1]);
            case "list":
                return new ListShowCommand(splitStr[1]);
            case "bye":
                return new ByeCommand();
            case "add":
                return parseAddShow(splitStr[1]);
            case "delete-all": // e.g. delete-all poto|lion king
                return parseDeleteAllOfShow(splitStr[1]);
            case "delete-one": // e.g. delete-one 2/10/2019|poto
                return parseDeleteOneOfShow(splitStr[1]);
            default:
                return null;
            }
        }
    }

    private static Command parsePostpone(String postponeDetails) {
        String[] splitStr = postponeDetails.trim().split("\\|", 3);
        // need to check if size of array is 3, if not throw exception
        String showName = splitStr[0].trim();
        String oldDate = splitStr[1].trim();
        String newDate = splitStr[2].trim();

        return new PostponeCommand(showName, oldDate, newDate);
    }

    private static Command parseAddShow(String showDetails) {
        String[] splitStr = showDetails.trim().split("\\|", 4);
        // need to check if size of array is 3, if not throw exception
        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();
        // need to add a NumberFormatException here
        double showCost = Double.parseDouble(splitStr[2]);
        double seatBasePrice = Double.parseDouble(splitStr[3]);

        return new AddCommand(showName, showDate, showCost, seatBasePrice);

    }

    // delete a single show on a particular date
    private static Command parseDeleteOneOfShow(String showDetails) {
        String[] splitStr = showDetails.trim().split("\\|");
        String showName = splitStr[0];
        String showDate = splitStr[1];
        // should add exception when no date is entered.
        return new DeleteOneCommand(showName, showDate);
    }

    // delete all instances of shows with specified name. Can contain multiple names, separated by pipe.
    private static Command parseDeleteAllOfShow(String deleteDetails) {
        String[] splitStr = deleteDetails.trim().split("\\|");

        return new DeleteAllCommand(splitStr);
    }

    private static Command parseViewSeating(String showDetails) {
        String[] splitStr = showDetails.trim().split("\\|");
        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();

        return new ViewSeatsCommand(showName, showDate);
    }

    private static Command parseSellSeats(String details) {
        String[] splitStr = details.trim().split("\\|");
        String showName = splitStr[0].trim();
        String showDate = splitStr[1].trim();
        String buyerName = splitStr[2].trim();
        if(splitStr.length == 4) {
            String seats = splitStr[3].trim();
            return new SellSeatCommand(showName, showDate, buyerName, seats);

        }

        return new SellSeatCommand(showName, showDate, showName);
    }
}
