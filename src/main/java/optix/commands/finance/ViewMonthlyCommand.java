package optix.commands.finance;

import optix.commands.Command;
import optix.commons.Model;
import optix.commons.Storage;
import optix.commons.model.ShowMap;
import optix.commons.model.Theatre;
import optix.exceptions.OptixInvalidDateException;
import optix.ui.Ui;
import optix.util.OptixDateFormatter;

import java.time.LocalDate;
import java.util.Map;

//@@author NicholasLiu97
public class ViewMonthlyCommand extends Command {
    private String month;
    private String year;

    private OptixDateFormatter formatter = new OptixDateFormatter();

    private static final String MESSAGE_NO_SHOW_FOUND = "☹ OOPS!!! There are no shows in %1$s %2$s.\n";

    private static final String MESSAGE_NOT_YET_CALCULATED = "☹ OOPS!!! The earnings for %1$s %2$s has not been"
            + " calculated. Try other months.\n";

    private static final String MESSAGE_SUCCESSFUL_LIST = "The earnings for %1$s %2$s is %3$.2f.\n";

    /**
     * Views the profit for a certain month.
     * @param splitStr String of format "MONTH YEAR"
     */
    public ViewMonthlyCommand(String splitStr) {
        String[] details = parseDetails(splitStr);
        this.month = details[0].trim();
        this.year = details[1].trim();
    }


    /**
     * Calculates the earnings for a certain month from the Optix file.
     * @param shows All shows found in ShowMap.
     * @param mth The month in numerical form.
     * @param yr The year.
     * @return A message String that contains the profit to show to the user.
     */
    private String findFromList(ShowMap shows, int mth, int yr) {
        String message;
        double profit = 0.0;

        ShowMap showsWanted = new ShowMap();
        for (Map.Entry<LocalDate, Theatre> entry : shows.entrySet()) {
            showsWanted.put(entry.getKey(), entry.getValue());
        }

        showsWanted.entrySet().removeIf(entry -> entry.getKey().getMonthValue() != mth
                || entry.getKey().getYear() != yr);

        if (showsWanted.isEmpty()) {
            message = String.format(MESSAGE_NO_SHOW_FOUND, month, year);
        } else {
            for (Map.Entry<LocalDate, Theatre> entry : showsWanted.entrySet()) {
                profit += entry.getValue().getProfit();
            }
            message = String.format(MESSAGE_SUCCESSFUL_LIST, month, year, profit);
        }
        return message;
    }

    @Override
    public String execute(Model model, Ui ui, Storage storage) {
        String message = "";
        int mth = formatter.getMonth(month.toLowerCase());
        int yr = formatter.getYear(year);

        try {
            if (mth == 0 || yr == 0) {
                throw new OptixInvalidDateException();
            }

            if (yr <= storage.getToday().getYear()) {
                if (mth < storage.getToday().getMonthValue()) {
                    message = findFromList(model.getShowsHistory(), mth, yr);
                } else if (mth == storage.getToday().getMonthValue()) {
                    message = String.format(MESSAGE_NOT_YET_CALCULATED, month, year);
                } else {
                    message = findFromList(model.getShows(), mth, yr);
                }
            } else {
                message = findFromList(model.getShows(), mth, yr);
            }
        } catch (OptixInvalidDateException e) {
            message = e.getMessage();
        } finally {
            ui.setMessage(message);
        }
        return "finance";
    }

    @Override
    public String[] parseDetails(String details) {
        return details.trim().split(" ");
    }

}
