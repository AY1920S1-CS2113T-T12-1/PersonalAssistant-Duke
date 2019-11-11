package optix.commands.shows;

import optix.commands.Command;
import optix.commons.Model;
import optix.commons.Storage;
import optix.exceptions.OptixException;
import optix.exceptions.OptixInvalidCommandException;
import optix.exceptions.OptixInvalidDateException;
import optix.ui.Ui;
import optix.util.OptixDateFormatter;

import java.time.LocalDate;
import java.util.logging.Level;

//@@author CheeSengg
public class RescheduleCommand extends Command {
    private String details;

    private OptixDateFormatter formatter = new OptixDateFormatter();

    private static final String MESSAGE_DOES_NOT_MATCH = "☹ OOPS!!! Did you get the wrong date or wrong show. \n"
            + "Try again!\n";

    private static final String MESSAGE_SHOW_NOT_FOUND = "☹ OOPS!!! The show cannot be found.\n";

    private static final String MESSAGE_SHOW_CLASH = "☹ OOPS!!! There already exists a show for %1$s.\n";

    private static final String MESSAGE_INVALID_NEW_DATE = "☹ OOPS!!! It is not possible to reschedule to the past.\n";

    private static final String MESSAGE_SUCCESSFUL = "%1$s has been rescheduled from %2$s to %3$s.\n";

    /**
     * Command to reschedule show.
     *
     * @param splitStr String containing "SHOW_NAME|OLD_DATE|NEW_DATE"
     */
    public RescheduleCommand(String splitStr) {
        this.details = splitStr;
        initLogger();
    }

    @Override
    public String execute(Model model, Ui ui, Storage storage) {
        String message = "";
        LocalDate today = storage.getToday();
        try {
            String[] details = parseDetails(this.details);
            String showName = details[0].trim();
            String oldDate = details[1].trim();
            String newDate = details[2].trim();

            if (!formatter.isValidDate(oldDate) || !formatter.isValidDate(newDate)) {
                OPTIXLOGGER.log(Level.WARNING, "Invalid date");
                throw new OptixInvalidDateException();
            }

            LocalDate localOldDate = formatter.toLocalDate(oldDate);
            LocalDate localNewDate = formatter.toLocalDate(newDate);

            if (localNewDate.compareTo(today) <= 0) {
                OPTIXLOGGER.log(Level.WARNING, "Invalid date");
                message = MESSAGE_INVALID_NEW_DATE;
            } else {
                if (!model.containsKey(localOldDate)) {
                    OPTIXLOGGER.log(Level.WARNING, MESSAGE_SHOW_NOT_FOUND);
                    message = MESSAGE_SHOW_NOT_FOUND;
                } else if (model.containsKey(localNewDate)) {
                    OPTIXLOGGER.log(Level.WARNING, MESSAGE_SHOW_CLASH);
                    message = String.format(MESSAGE_SHOW_CLASH, newDate);
                } else if (!model.hasSameName(localOldDate, showName)) {
                    OPTIXLOGGER.log(Level.WARNING, MESSAGE_DOES_NOT_MATCH);
                    message = MESSAGE_DOES_NOT_MATCH;
                } else {
                    model.rescheduleShow(localOldDate, localNewDate);
                    storage.write(model.getShows());
                    message = String.format(MESSAGE_SUCCESSFUL, showName, oldDate, newDate);
                }
            }
        } catch (OptixException e) {
            message = e.getMessage();
            ui.setMessage(message.toString());
            return "";
        }
        ui.setMessage(message.toString());
        return "show";
    }

    @Override
    public String[] parseDetails(String details) throws OptixInvalidCommandException {
        String[] detailsArray = details.trim().split("\\|", 3);
        if ((detailsArray.length) != 3) {
            throw new OptixInvalidCommandException();
        }
        return detailsArray;
    }

}
