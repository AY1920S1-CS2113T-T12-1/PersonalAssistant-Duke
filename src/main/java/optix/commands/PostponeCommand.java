package optix.commands;

import optix.Ui;
import optix.constant.OptixResponse;
import optix.core.Storage;
import optix.core.Theatre;
import optix.util.OptixDateFormatter;
import optix.util.ShowMap;

import java.time.LocalDate;

public class PostponeCommand extends Command {
    private String showName;
    private String oldDate;
    private String newDate;

    private OptixResponse response = new OptixResponse();
    private OptixDateFormatter formatter = new OptixDateFormatter();

/**
 * Sets up the command.
 * @param showName is the name of the show to postpone
 * @param oldDate is the current date of the show
 * @param newDate is the new date of the show
 */
public PostponeCommand(String showName, String oldDate, String newDate) {
        // need to check if both dates are valid if not throw exception
        // need to check if the event was completed in the past. Past event shouldn't be postponed.
        this.showName = showName;
        this.oldDate = oldDate;
        this.newDate = newDate;
    }

/**
 * Executes the command.
 * @param shows The list of performance that are upcoming.
 * @param ui The User Interface that reads user input and response to user.
 * @param storage The filepath of txt file which data are being stored.
 */
@Override
    public void execute(ShowMap shows, Ui ui, Storage storage) {
        String message;
        LocalDate today = storage.getToday();
        LocalDate localOldDate = formatter.toLocalDate(oldDate);
        LocalDate localNewDate = formatter.toLocalDate(newDate);


        if (localOldDate.compareTo(today) <= 0) {
            message = response.SHOW_OVER;
        } else if (localNewDate.compareTo(today) <= 0) {

            message = response.POSTPONE_PAST;
        } else {
            if (!shows.containsKey(localOldDate)) {
                message = response.SHOW_NOT_FOUND;
            } else if (shows.containsKey(localNewDate)) {
                message = response.POSTPONE_CLASH + newDate + '.' + "\n";
            } else if (!shows.get(localOldDate).hasSameName(showName)) {
                message = response.SHOW_DOES_NOT_MATCH;
            } else {
                Theatre postponedShow = shows.removeShow(localOldDate);
                shows.put(localNewDate, postponedShow);

                message = String.format("%s has been postponed from %s to %s.\n", showName, oldDate, newDate);
            }
        }

        ui.setMessage(message);
    }

    @Override
    public boolean isExit() {
        return super.isExit();
    }
}
