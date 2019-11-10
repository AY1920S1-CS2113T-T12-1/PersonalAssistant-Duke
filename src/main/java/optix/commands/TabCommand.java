package optix.commands;

import optix.commons.Model;
import optix.commons.Storage;
import optix.exceptions.OptixInvalidCommandException;
import optix.ui.Ui;

public class TabCommand extends Command {
    String commandWord;

    private  static final String MESSAGE_ARCHIVE = "Here is your list of archived shows.\n";
    private static final String MESSAGE_SHOW = "Here is your list of scheduled shows.\n";
    private static final String MESSAGE_FINANCE = "Here is your list of projected earnings.\n";
    private static final String MESSAGE_HELP = "Here are the list of commands you can use.\n";

    public TabCommand(String commandWord) {
        this.commandWord = commandWord.trim().toLowerCase();
    }

    @Override
    public String execute(Model model, Ui ui, Storage storage) {
        StringBuilder message = new StringBuilder();
        try {
            switch (commandWord) {
            case "archive":
                message.append(MESSAGE_ARCHIVE);
                message.append(model.listShowHistory());
                ui.setMessage(message.toString());
                break;
            case "finance":
                message.append(MESSAGE_FINANCE);
                message.append(model.listFinance());
                ui.setMessage(message.toString());
                break;
            case "help":
                ui.setMessage(MESSAGE_HELP);
                break;
            default:
                throw new OptixInvalidCommandException();
            }
        } catch (OptixInvalidCommandException e) {
            ui.setMessage(e.getMessage());
        }
        return commandWord;
    }

    @Override
    public String[] parseDetails(String details) {
        return new String[0];
    }
}
