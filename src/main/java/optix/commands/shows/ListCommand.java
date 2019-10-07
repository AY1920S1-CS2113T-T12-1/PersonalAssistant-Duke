package optix.commands.shows;

import optix.Ui;
import optix.commands.Command;
import optix.core.Storage;
import optix.core.Theatre;
import optix.util.ShowMap;

import java.time.LocalDate;
import java.util.Map;

public class ListCommand extends Command {
    private static final String MESSAGE_LIST_FOUND = "Here are the list of shows:\n";
    private static final String MESSAGE_LIST_NOT_FOUND = "☹ OOPS!!! There are no shows in the near future.\n";
    private static final String MESSAGE_ENTRY = "%1$d. %2$s (on: %3$s)\n";

    @Override
    public void execute(ShowMap shows, Ui ui, Storage storage) {
        StringBuilder message = new StringBuilder();
        LocalDate today = storage.getToday();

        int counter = 1;

        if (!shows.isEmpty()) {
            message.append(MESSAGE_LIST_FOUND);
            for (Map.Entry<LocalDate, Theatre> entry : shows.entrySet()) {
                Theatre show = entry.getValue();
                LocalDate showDate = entry.getKey();

                if (showDate.compareTo(today) > 0) {
                    message.append(String.format(MESSAGE_ENTRY, counter, show.getShowName(), showDate));
                    counter++;
                }
            }
        }

        if (shows.isEmpty() || counter == 1) {
            message = new StringBuilder(MESSAGE_LIST_NOT_FOUND);
        }

        ui.setMessage(message.toString());
    }

    @Override
    public boolean isExit() {
        return super.isExit();
    }
}
