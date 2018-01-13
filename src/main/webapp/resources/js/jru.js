/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    jru.js
 *  Created: 2018.01.13. 21:18:32
 *
 *  ------------------------------------------------------------------------------------
 */

/* global PrimeFaces, PF */

/**
 * Primefaces Calendar Magyar lokalizáció
 * @type type
 */
try {
    PrimeFaces.locales['hu'] = {
        closeText: 'Bezárás',
        prevText: 'Előző',
        nextText: 'Következő',
        monthNames: ['Január', 'Február', 'Március', 'Április', 'Május', 'Június', 'Július', 'Augusztus', 'Szeptember', 'Október', 'November', 'December'],
        monthNamesShort: ['Jan', 'Febr', 'Márc', 'Ápr', 'Máj', 'Jún', 'Júl', 'Aug', 'Szept', 'Okt', 'Nov', 'Dec'],
        dayNames: ['Vasárnap', 'Hétfő', 'Kedd', 'Szerda', 'Csütörtök', 'Péntek', 'Szombat'],
        dayNamesShort: ['Vas', 'H', 'K', 'Sze', 'Csüt', 'P', 'Szo'],
        dayNamesMin: ['V', 'H', 'K', 'Sze ', 'Cs', 'P ', 'Szo'],
        weekHeader: 'Hét',
        firstDay: 1, //hétfő az első nap
        isRTL: false,
        showMonthAfterYear: true,
        yearSuffix: '.',
        timeOnlyTitle: 'Csak óra',
        timeText: 'Időpont',
        hourText: 'Óra',
        minuteText: 'Perc',
        secondText: 'Másodperc',
        currentText: 'Ma',
        ampm: false,
        month: 'Hónap',
        week: 'Hét',
        day: 'Nap',
        allDayText: 'Egész nap',
        aria: {
            'paginator.PAGE': 'Lap {0}',
            'calendar.BUTTON': 'Naptár',
            'datatable.sort.ASC': 'rendezés növekvő sorrendben',
            'datatable.sort.DESC': 'rendezés csökkenő sorrendben',
            'columntoggler.CLOSE': 'Bezár'
        }

    };
} catch (e) {
    //Ha nincs PrimeFaces ... :(
}

/**
 * JS segédfüggvények
 */
var JRU = {

    /**
     * Ajax Update PF library segítségével
     * @param {type} source forrás Id
     * @param {type} form forrás form
     * @param {type} update frissítendő komponens
     * @returns {undefined}
     */
    update: function (source, form, update) {
        PrimeFaces.ab({
            s: source,
            f: form,
            u: update
        });
    },

    /**
     * Update and show PrimeFaces Dialog
     *
     * @param {type} fromId forrás id
     * @param {type} fromForm forrás form
     * @param {type} dglWidgetWar PF dialógus WidgetWar
     * @returns {undefined} -
     */
    updateAndShowDialog: function (fromId, fromForm, dglWidgetWar) {

        //PF Ajax hívás
        PrimeFaces.ab(
                {s: fromId, // source
                    f: fromForm, // formId
                    u: dglWidgetWar, // update
                    onco: // onComplete
                            function (xhr, status, args) {
                                PF(dglWidgetWar).show(); //Dialogus show
                            }, pa: arguments[0]
                }
        );
    }
};
