package optix.commands.parser;

import optix.commands.Command;
import optix.commons.Model;
import optix.commons.Storage;
import optix.exceptions.OptixException;
import optix.ui.Ui;
import optix.util.Parser;


import java.util.HashMap;

public class AddAliasCommand extends Command {
    private String newAlias;
    private String command;
    /**
     * Command to add a new alias to the command alias map.
     * @param details String containing "NEW_ALIAS|COMMAND"
     */
    public AddAliasCommand(String details) {
        String[] detailsArray = parseDetails(details);
        this.newAlias = detailsArray[0];
        this.command = detailsArray[1];
    }

    /**
     * Processes user input to be stored, queried, modified in ShowMap,
     * to show response by program in ui and store existing data in Storage.
     *
     * @param model   The data structure holding all the information.
     * @param ui      The User Interface that reads user input and response to user.
     * @param storage The filepath of txt file which data are being stored.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) throws OptixException {
        // create new parser. checks for duplicate alias/ non existent command is in addAlias method.
        Parser newParser = new Parser();
        String message;
        try {
            newParser.addAlias(newAlias, command);
            message = String.format("The new alias %s was successfully paired to the command %s", newAlias, command);
        } catch (OptixException e) {
            message = e.getMessage();
        }
        ui.setMessage(message);
    }

    @Override
    public String[] parseDetails(String details) {
        return details.split("\\|",2);
    }
}

