package optix.commands.shows;

import optix.Ui;
import optix.commands.Command;
import optix.core.Storage;
import optix.core.Theatre;
import optix.util.ShowMap;

import java.time.LocalDate;
import java.util.Map;

public class ListShowCommand extends Command {
    private String showName;

    private static final String MESSAGE_FOUND_SHOW = "The show %1$s is showing on the following following dates: \n";

    private static final String MESSAGE_SHOW_NOT_FOUND = "☹ OOPS!!! The show cannot be found.\n";

    private static final String MESSAGE_ENTRY = "%1$d. %2$s\n";

    public ListShowCommand(String showName) {
        this.showName = showName;
    }


    @Override
    public void execute(ShowMap shows, Ui ui, Storage storage) {
        StringBuilder message = new StringBuilder(String.format(MESSAGE_FOUND_SHOW, showName));

        boolean hasShow = false;
        String today = LocalDate.now().toString();

        int counter = 1;

        for (Map.Entry<LocalDate, Theatre> entry : shows.entrySet()) {
            String showDate = entry.getKey().toString();

            if(showDate.compareTo(today) <= 0) {
                continue;
            }

            // Can add to check whether the show has seats available. If not seats are available we can remove it from the listing.
            if (entry.getValue().hasSameName(showName.trim())) {
                hasShow = true;
                message.append(String.format(MESSAGE_ENTRY, counter, showDate));
                counter++;
            }
        }
        
        if (!hasShow) {
            message = new StringBuilder(MESSAGE_SHOW_NOT_FOUND);
        }

        ui.setMessage(message.toString());
    }

    @Override
    public boolean isExit() {
        return super.isExit();
    }


}
