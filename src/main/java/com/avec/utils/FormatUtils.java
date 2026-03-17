package com.avec.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilitaires pour le formatage des données
 */
public class FormatUtils {

    // Formateurs de date
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter DATE_FORMATTER_LONG =
            DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");

    // Formateur de monnaie (FCFA)
    private static final NumberFormat CURRENCY_FORMAT;

    static {
        CURRENCY_FORMAT = NumberFormat.getNumberInstance(Locale.FRENCH);
        CURRENCY_FORMAT.setMinimumFractionDigits(0);
        CURRENCY_FORMAT.setMaximumFractionDigits(0);
    }

    /**
     * Formate un montant en FCFA
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 FCFA";
        }
        return CURRENCY_FORMAT.format(amount) + " FCFA";
    }

    /**
     * Formate un montant en FCFA (pour les doubles)
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount) + " FCFA";
    }

    /**
     * Formate un montant sans le symbole (pour les calculs)
     */
    public static String formatCurrencyPlain(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return CURRENCY_FORMAT.format(amount);
    }

    /**
     * Formate une date au format dd/MM/yyyy
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formate une date et heure
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Formate une date au format long (ex: lundi 15 mars 2024)
     */
    public static String formatDateLong(LocalDate date) {
        if (date == null) {
            return "";
        }
        String formatted = date.format(DATE_FORMATTER_LONG);
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }

    /**
     * Formate un pourcentage
     */
    public static String formatPercentage(double value) {
        return String.format("%.1f%%", value);
    }

    /**
     * Formate un pourcentage avec 2 décimales
     */
    public static String formatPercentage(BigDecimal value) {
        if (value == null) {
            return "0%";
        }
        return String.format("%.2f%%", value);
    }

    /**
     * Formate un numéro de téléphone
     */
    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "";
        }

        // Nettoyer le numéro
        String cleaned = phone.replaceAll("[^0-9]", "");

        // Format sénégalais: 77 123 45 67
        if (cleaned.length() == 9) {
            return cleaned.substring(0, 2) + " " +
                    cleaned.substring(2, 5) + " " +
                    cleaned.substring(5, 7) + " " +
                    cleaned.substring(7);
        }

        return phone;
    }

    /**
     * Met la première lettre en majuscule
     */
    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Formate un nom (tout en majuscules)
     */
    public static String formatNom(String nom) {
        if (nom == null) {
            return "";
        }
        return nom.toUpperCase();
    }

    /**
     * Formate un prénom (première lettre en majuscule)
     */
    public static String formatPrenom(String prenom) {
        return capitalizeFirst(prenom);
    }

    /**
     * Formate un nom complet (Prénom NOM)
     */
    public static String formatNomComplet(String prenom, String nom) {
        StringBuilder sb = new StringBuilder();
        if (prenom != null && !prenom.isEmpty()) {
            sb.append(capitalizeFirst(prenom));
        }
        if (nom != null && !nom.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(nom.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Tronque un texte à une longueur maximale
     */
    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Convertit un montant en toutes lettres (simplifié)
     */
    public static String numberToWords(BigDecimal amount) {
        if (amount == null) {
            return "zéro";
        }

        long partieEntiere = amount.longValue();
        if (partieEntiere == 0) {
            return "zéro";
        }

        // Implémentation simplifiée - à améliorer selon les besoins
        return String.valueOf(partieEntiere);
    }

    /**
     * Formate une durée en secondes en format lisible
     */
    public static String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + " seconde(s)";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute(s)";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "h " + minutes + "min";
        }
    }
}