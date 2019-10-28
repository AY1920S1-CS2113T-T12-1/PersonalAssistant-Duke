package optix.commands;

import optix.commons.Model;
import optix.commons.Storage;
import optix.exceptions.OptixInvalidCommandException;
import optix.ui.Ui;

public class TabCommand extends Command {
    String commandWord;

    private  static final String MESSAGE_ARCHIVE = "Here is your list of archived shows.";
    private static final String MESSAGE_SHOW = "Here is your list of scheduled shows.";
    private static final String MESSAGE_FINANCE = "Here is your list of projected earnings";

    public TabCommand(String commandWord) {
        this.commandWord = commandWord.trim().toLowerCase();
    }

    @Override
    public String execute(Model model, Ui ui, Storage storage) {
        try {
            switch (commandWord) {
            case "archive":
                model.setShowsGUI(model.getShowsHistory());
                ui.setMessage(MESSAGE_ARCHIVE);
                break;
            case "show":
                model.setShowsGUI(model.getShows());
                ui.setMessage(MESSAGE_SHOW);
                break;
            case "finance":
                model.setShowsGUI(model.getShows());
                ui.setMessage(MESSAGE_FINANCE);
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
