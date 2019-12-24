function formatDuration(time) {
    var secs = time;
    var mins = 0;
    var hours = 0;
    var days = 0;
    var weeks = 0;

    mins = ((secs - (secs % 60)) / 60);
    secs = (secs % 60);

    hours = ((mins - (mins % 60)) / 60);
    mins = (mins % 60);

    days = ((hours - (hours % 8)) / 8);
    hours = (hours % 8);

    weeks = ((days - (days % 5)) / 5);
    days = (days % 5);

    var timeSpent = '';
    if (weeks > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += weeks + 'w';
    }
    if (days > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += days + 'd';
    }
    if (hours > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += hours + 'h';
    }
    if (mins > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += mins + 'm';
    }

    if (timeSpent === '')
        timeSpent = '0';

    return timeSpent
}

function getDurationSeconds(duration) {
    if (!duration)
        return 0;

    var seconds = 0;
    var parts = duration.split(' ');
    for (var i = 0; i < parts.length; i++) {
        var dur = parts[i];
        if (dur.endsWith('w'))
            seconds += parseInt(dur.replace('w', '')) * 60 * 60 * 8 * 5;
        else if (dur.endsWith('d'))
            seconds += parseInt(dur.replace('d', '')) * 60 * 60 * 8;
        else if (dur.endsWith('h'))
            seconds += parseInt(dur.replace('h', '')) * 60 * 60;
        else
            seconds += parseInt(dur.replace('m', '')) * 60 * 60;
    }
    return seconds;
}

function reformatDuration(duration) {
    // console.log(getDurationSeconds(duration));
    // console.log(formatDuration(getDurationSeconds(duration)));
    return formatDuration(getDurationSeconds(duration));
}

function validateDuration(duration) {
    try {
        var seconds = getDurationSeconds(duration);
        // console.log(seconds);
        if ((seconds > 0) && formatDuration(seconds))
            return true;
        else
            return false;
    } catch (error) {
        // console.log(error);
        return false;
    }
}

function workingDaysBetweenDates(startDate, endDate){
    /* Two working days and an sunday (not working day) */
    // var holidays = ['2016-05-03', '2016-05-05', '2016-05-07'];
    // var startDate = d0;
    // var endDate = d1;

// Validate input
    if (endDate < startDate) {
        return 0;
    }

// Calculate days between dates
    var millisecondsPerDay = 86400 * 1000; // Day in milliseconds
    startDate.setHours(0, 0, 0, 1);  // Start just after midnight
    endDate.setHours(23, 59, 59, 999);  // End just before midnight
    var diff = endDate - startDate;  // Milliseconds between datetime objects
    var days = Math.ceil(diff / millisecondsPerDay);

    // Subtract two weekend days for every week in between
    var weeks = Math.floor(days / 7);
    days -= weeks * 2;

    // Handle special cases
    var startDay = startDate.getDay();
    var endDay = endDate.getDay();

    // Remove weekend not previously removed.
    if (startDay - endDay > 1) {
        days -= 2;
    }
    // Remove start day if span starts on Sunday but ends before Saturday
    if (startDay == 0 && endDay != 6) {
        days--;
    }
    // Remove end day if span ends on Saturday but starts after Sunday
    if (endDay == 6 && startDay != 0) {
        days--;
    }
    /* Here is the code */
    // holidays.forEach(day => {
    //     if ((day >= d0) && (day <= d1)) {
    //     /* If it is not saturday (6) or sunday (0), substract it */
    //     if ((parseDate(day).getDay() % 6) != 0) {
    //         days--;
    //     }
    // }
// });
    return days;
}

// function parseDate(input) {
//     // Transform date from text to date
//     var parts = input.match(/(\d+)/g);
//     // new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
//     return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
// }

