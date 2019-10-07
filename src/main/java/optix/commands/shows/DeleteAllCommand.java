package optix.commands.shows;

import optix.Ui;
import optix.commands.Command;
import optix.core.Storage;
import optix.core.Theatre;
import optix.util.ShowMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class DeleteAllCommand extends Command {
    private String[] showNames;

    private static final String MESSAGE_SUCCESSFUL = "Noted. These are the deleted entries:\n";

    private static final String MESSAGE_UNSUCCESSFUL = "Sorry, these shows were not found:\n";

    public DeleteAllCommand(String[] showNames) {
        this.showNames = showNames;
    }

    @Override
    /*
     * Command that deletes all instances of a specific show.
     */
    public void execute(ShowMap shows, Ui ui, Storage storage) {
        StringBuilder message = new StringBuilder();
        ArrayList<String> deletedShows = new ArrayList<>();
        ArrayList<String> missingShows = new ArrayList<>();

        for (String show: this.showNames) {
            boolean isFound = false;
            ArrayList<Map.Entry<LocalDate, Theatre>> entryArrayList = new ArrayList<>();
            for (Map.Entry<LocalDate, Theatre> entry : shows.entrySet()) {
                if (entry.getValue().hasSameName(show.trim())) {
                    String showDescription = entry.getKey().toString() + ' ' + entry.getValue().getShowName();
                    entryArrayList.add(entry);
                    deletedShows.add(showDescription);
                    isFound = true;
                }
            }
            // add show to missing show list if it's not found.
            if (!isFound) {
                missingShows.add(show);
            }
            // remove entry from shows.
            for (Map.Entry<LocalDate, Theatre> entry: entryArrayList) {
                shows.remove(entry.getKey(), entry.getValue());
            }
        }
        if (!deletedShows.isEmpty()) {
            message.append(MESSAGE_SUCCESSFUL);
            for (String infoStrings: deletedShows) {
                message.append(infoStrings.trim()).append('\n');
            }
        }
        if (!missingShows.isEmpty()) {
            message.append(MESSAGE_UNSUCCESSFUL);
            for (String missingShow: missingShows) {
                message.append(missingShow.trim()).append('\n');
            }
        }
        ui.setMessage(message.toString());
    }
}
