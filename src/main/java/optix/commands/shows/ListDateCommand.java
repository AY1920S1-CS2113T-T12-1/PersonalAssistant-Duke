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

//@@author CheeSengg
public class ListDateCommand extends Command {
    private final String monthOfYear;
    private String formattedMonthOfYear;

    private OptixDateFormatter formatter = new OptixDateFormatter();

    private static final String MESSAGE_FOUND_SHOW = "These shows are showing on %1$s: \n";

    private static final String MESSAGE_NO_SHOWS_FOUND = "☹ OOPS!!! There are no shows on %1$s.\n";

    public ListDateCommand(String monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    @Override
    public String execute(Model model, Ui ui, Storage storage) {
<<<<<<< HEAD
        StringBuilder message = new StringBuilder();
=======
        String[] splitStr = monthOfYear.trim().split(" ");
        int year = formatter.getYear(splitStr[1].trim());
        int month = formatter.getMonth(splitStr[0].trim().toLowerCase());

        StringBuilder message = new StringBuilder(String.format(MESSAGE_FOUND_SHOW, monthOfYear));

>>>>>>> 6465a3b127cc80c351305265aac57ac462b07d8e
        try {
            String[] splitStr = monthOfYear.trim().split(" ");
            if (splitStr.length != 2) {
                throw new OptixInvalidCommandException();
            }
            int year = formatter.getYear(splitStr[1].trim());
            int month = formatter.getMonth(splitStr[0].trim().toLowerCase());
            if (year == 0 || month == 0) {
                throw new OptixInvalidDateException();
            }
            formattedMonthOfYear = formatter.intToMonth(month) + ' ' + year;
            LocalDate startOfMonth = formatter.getStartOfMonth(year, month);
            LocalDate endOfMonth = formatter.getEndOfMonth(year, month);
            message.append(String.format(MESSAGE_FOUND_SHOW, formattedMonthOfYear));
            message.append(model.listShow(startOfMonth, endOfMonth));
            if (!hasShow(message.toString())) {
                message = new StringBuilder(String.format(MESSAGE_NO_SHOWS_FOUND, formattedMonthOfYear));
            }
        } catch (OptixException e) {
            message.append(e.getMessage());
        } finally {
            ui.setMessage(message.toString());
        }
        return "show";
    }

    /**
     * Dummy Command. Not used
     * @param details n.a
     * @return n.a.
     */
    @Override
    public String[] parseDetails(String details) {
        return new String[0];
    }

    private boolean hasShow(String message) {
        return !message.equals(String.format(MESSAGE_FOUND_SHOW, formattedMonthOfYear));
    }


}
