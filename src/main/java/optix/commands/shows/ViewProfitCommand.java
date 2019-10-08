package optix.commands.shows;

import optix.commons.Model;
import optix.commons.model.Show;
import optix.commons.model.ShowHistoryMap;
import optix.ui.Ui;
import optix.commands.Command;
import optix.commons.Storage;
import optix.commons.model.Theatre;
import optix.exceptions.OptixInvalidDateException;
import optix.util.OptixDateFormatter;
import optix.commons.model.ShowMap;

import java.time.LocalDate;

public class ViewProfitCommand extends Command{

    private String showName;
    private String showDate;

    private OptixDateFormatter formatter = new OptixDateFormatter();

    private static final String MESSAGE_SHOW_NOT_FOUND = "☹ OOPS!!! The show cannot be found.\n";

    private static final String MESSAGE_DOES_NOT_MATCH = "☹ OOPS!!! Did you get the wrong date or wrong show. \n"
            + "Try again!\n";

    private static final String MESSAGE_SUCCESSFUL = "The profit for %1$s on %2$s is %3$s\n";

    public ViewProfitCommand(String showName, String showDate) {
        this.showName = showName;
        this.showDate = showDate;
    }

    @Override
    public void execute(Model model, Ui ui, Storage storage) {

        String message = "";

        try {
            if (!formatter.isValidDate(showDate)) {
                throw new OptixInvalidDateException();
            }

            LocalDate localDate = formatter.toLocalDate(showDate);

            if (localDate.compareTo(storage.getToday()) <= 0) { //in archive list
                ShowHistoryMap showsHistory = model.getShowsHistory();
                if (!showsHistory.containsKey(localDate)) { //date not found
                    message = MESSAGE_SHOW_NOT_FOUND;
                } else if (!showsHistory.get(localDate).hasSameName(showName)) {
                    message = MESSAGE_DOES_NOT_MATCH;
                } else {
                    message = String.format(MESSAGE_SUCCESSFUL, showName, showDate,
                            showsHistory.get(localDate).getProfit());
                }
            } else {
                ShowMap shows = model.getShows();
                if (!shows.containsKey(localDate)) {
                    message = MESSAGE_SHOW_NOT_FOUND;
                } else if (!shows.get(localDate).hasSameName(showName)) {
                    message = MESSAGE_DOES_NOT_MATCH;
                } else {
                    message = String.format(MESSAGE_SUCCESSFUL, showName, showDate,
                            shows.get(localDate).getProfit());
                }
            }
        } catch (OptixInvalidDateException e) {
            message = e.getMessage();
        } finally {
            ui.setMessage(message);
        }
    }
}
