package optix.commands.shows;

import optix.commands.Command;
import optix.commons.Model;
import optix.commons.Storage;
import optix.commons.model.ShowMap;
import optix.ui.Ui;

import java.util.logging.Level;

//@@author CheeSengg
public class ListCommand extends Command {
    private static final String MESSAGE_LIST_FOUND = "Here are the list of shows:\n";

    private static final String MESSAGE_LIST_NOT_FOUND = "☹ OOPS!!! There are no shows in the near future.\n";

    public ListCommand() {
        initLogger();
    }

    @Override
    public String execute(Model model, Ui ui, Storage storage) {
        ShowMap shows = model.getShows();
        StringBuilder message = new StringBuilder();

        if (!shows.isEmpty()) {
            message.append(MESSAGE_LIST_FOUND);
            message.append(model.listShow());
        } else {
            message = new StringBuilder(MESSAGE_LIST_NOT_FOUND);
            OPTIXLOGGER.log(Level.WARNING, MESSAGE_LIST_NOT_FOUND);
        }
        ui.setMessage(message.toString());
        return "show";
    }

    @Override
    public String[] parseDetails(String details) {
        return new String[0];
    }

}
