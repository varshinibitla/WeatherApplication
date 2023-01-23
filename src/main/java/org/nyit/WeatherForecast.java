package org.nyit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class WeatherForecast extends JFrame implements ActionListener {
    Component component = SwingUtilities.getRoot(this);
    JPanel jPanel = new JPanel();
    JLabel locationLabel = new JLabel("Location");
    JComboBox<String> location;
    JLabel timeZoneLabel = new JLabel("TimeZone");
    JComboBox<String> timeZone;
    JRadioButton celsius = new JRadioButton("Celsius");
    JRadioButton fahrenheit = new JRadioButton("Fahrenheit");
    ButtonGroup buttonGroup = new ButtonGroup();
    JButton continueButton = new JButton("Continue");
    JLabel error = new JLabel();
    JFrame jFrame = new JFrame();
    JLabel dayLabel = new JLabel("Date", SwingConstants.CENTER);
    JComboBox<String> day;
    JLabel hourLabel = new JLabel("Hour", SwingConstants.CENTER);
    JComboBox<String> hour;
    JButton back = new JButton("Back");
    JButton submitButton = new JButton("Ok");
    JLabel temperature = new JLabel();
    JLabel sunrise = new JLabel();
    JLabel sunset = new JLabel();
    JLabel minimumTemperature = new JLabel();
    JLabel maximumTemperature = new JLabel();


    //info and layout of first window that pops up on the screen
    public WeatherForecast() {
        jPanel.setLayout(null);
        locationLabel.setBounds(25, 25, 100, 50);
        jPanel.add(locationLabel);

        //list of location inputs for the dropdown location options
        String[] locationOptions = {"Jersey City", "San Diego", "Dallas", "Niagara Falls", "Tampa"};
        location = new JComboBox<>(locationOptions);
        location.setBounds(150, 25, 100, 50);
        jPanel.add(location);

        //time zone label and dropdown button option info
        timeZoneLabel.setBounds(25, 100, 100, 50);
        jPanel.add(timeZoneLabel);
        String[] timezoneOptions = {"EST", "PST", "CST"};
        timeZone = new JComboBox<>(timezoneOptions);
        timeZone.setBounds(150, 100, 100, 50);
        jPanel.add(timeZone);

        //celsius and fahrenheit option buttons info
        celsius.setBounds(25, 175, 100, 50);
        celsius.setActionCommand("Celsius");
        jPanel.add(celsius);
        fahrenheit.setBounds(150, 175, 100, 50);
        fahrenheit.setActionCommand("Fahrenheit");
        fahrenheit.setSelected(true);
        jPanel.add(fahrenheit);
        //adding c and f buttons
        buttonGroup.add(celsius);
        buttonGroup.add(fahrenheit);
        continueButton.setBounds(75, 250, 100, 50);
        continueButton.addActionListener(this);
        jPanel.add(continueButton);
        error.setBounds(50, 325, 200, 50);
        jPanel.add(error);
        add(jPanel); //adding the panel to the frame
    }

    //main method that runs the program
    public static void main(String[] args) {
        WeatherForecast weatherForecast = new WeatherForecast();
        weatherForecast.setSize(500, 450);
        weatherForecast.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        weatherForecast.setVisible(true);
        weatherForecast.setResizable(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            StringBuilder uri = new StringBuilder();
            uri.append("https://api.open-meteo.com/v1/forecast?"); //open API
            uri.append("current_weather=true");
            uri.append("&hourly=temperature_2m");
            uri.append("&daily=sunrise,sunset,temperature_2m_min,temperature_2m_max");
            String selectedLocation = location.getItemAt(location.getSelectedIndex());
            //setting up latitude and longitudes for each City
            if (selectedLocation.equalsIgnoreCase("Jersey City")) {
                uri.append("&latitude=").append("40.73").append("&longitude=").append("-74.08");
            } else if (selectedLocation.equalsIgnoreCase("San Diego")) {
                uri.append("&latitude=").append("32.72").append("&longitude=").append("-117.16");
            } else if (selectedLocation.equalsIgnoreCase("Dallas")) {
                uri.append("&latitude=").append("32.78").append("&longitude=").append("-96.81");
            } else if (selectedLocation.equalsIgnoreCase("Niagara Falls")) {
                uri.append("&latitude=").append("43.09").append("&longitude=").append("-79.06");
            } else if (selectedLocation.equalsIgnoreCase("Tampa")) {
                uri.append("&latitude=").append("27.95").append("&longitude=").append("-82.46");
            }
            String selectedTemperatureUnit = buttonGroup.getSelection().getActionCommand();
            if (selectedTemperatureUnit.equalsIgnoreCase("Fahrenheit")) {
                uri.append("&temperature_unit=fahrenheit");
            }
            String selectedTimeZone = timeZone.getItemAt(timeZone.getSelectedIndex());
            if (selectedTimeZone.equalsIgnoreCase("EST")) {
                uri.append("&timezone=America%2FNew_York");
            } else if (selectedTimeZone.equalsIgnoreCase("PST")) {
                uri.append("&timezone=America%2FLos_Angeles");
            } else if (selectedTimeZone.equalsIgnoreCase("CST")) {
                uri.append("&timezone=America%2FDenver");
            }
            StringBuilder stringBuilder = new StringBuilder();
            URL url = new URL(uri.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                String output;
                while ((output = reader.readLine()) != null) {
                    stringBuilder.append(output);
                }
            }
            //selected day and hour
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(stringBuilder.toString());
            String selectedDayString = null;
            String selectedHourString = null;
            if (day != null && hour != null) {
                selectedDayString = day.getItemAt(day.getSelectedIndex());
                selectedHourString = hour.getItemAt(hour.getSelectedIndex());
            }
            String currentTemperatureString = null;
            String dayAndHourString;
            String dayString = null;
            String hourString;
            if (selectedDayString != null && selectedHourString != null) {
                for (int i = 0; i <= 167; i++) {
                    dayAndHourString = jsonNode.get("hourly").get("time").get(i).toString();
                    dayString = dayAndHourString.substring(1, 11);
                    hourString = dayAndHourString.substring(12, 14);
                    if (selectedDayString.equalsIgnoreCase(dayString) &&
                        selectedHourString.equalsIgnoreCase(hourString)) {
                        currentTemperatureString = jsonNode.get("hourly").get("temperature_2m").get(i).toString();
                        break;
                    }
                }
            } else {
                currentTemperatureString = jsonNode.get("current_weather").get("temperature").toString();
                dayAndHourString = jsonNode.get("current_weather").get("time").toString();
                dayString = dayAndHourString.substring(1, 11);
                hourString = dayAndHourString.substring(12, 14);
                String[] dayOptions = new String[7];
                for (int i = 0; i <= 6; i++) {
                    dayOptions[i] = LocalDate.parse(dayString).plusDays(i).toString();
                }
                //drop down menu for the day options
                day = new JComboBox<>(dayOptions);
                day.setSelectedItem(dayString);
                String[] hourOptions =
                        {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
                         "16", "17", "18", "19", "20", "21", "22", "23"};
                hour = new JComboBox<>(hourOptions);
                hour.setSelectedItem(hourString);
            }
            //display generated weather info (temp, sunrise, sunset, min temp, max temp)
            temperature.setText(currentTemperatureString);
            temperature.setForeground(Color.BLUE);
            for (int j = 0; j <= 6; j++) {
                String sunriseString = jsonNode.get("daily").get("sunrise").get(j).toString();
                String sunsetString = jsonNode.get("daily").get("sunset").get(j).toString();
                String minimumTemperatureString = jsonNode.get("daily").get("temperature_2m_min").get(j).toString();
                String maximumTemperatureString = jsonNode.get("daily").get("temperature_2m_max").get(j).toString();
                if (sunriseString.substring(1, 11).equalsIgnoreCase(dayString) &&
                    sunsetString.substring(1, 11).equalsIgnoreCase(dayString)) {
                    sunrise.setText("Sunrise : " + sunriseString.substring(12, 17));
                    sunrise.setForeground(Color.BLUE);
                    sunset.setText("Sunset : " + sunsetString.substring(12, 17));
                    sunset.setForeground(Color.BLUE);
                    minimumTemperature.setText("Min Temperature: " + minimumTemperatureString);
                    minimumTemperature.setForeground(Color.BLUE);
                    maximumTemperature.setText("Max Temperature:" + maximumTemperatureString);
                    maximumTemperature.setForeground(Color.BLUE);
                }
            }
            //jFrame layouts settings
            jFrame.setLayout(null);
            jFrame.setSize(500, 500);
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jFrame.setVisible(true);
            jFrame.setResizable(true);

            //adding an image Icon to the Frame
            ImageIcon image = new ImageIcon("logo.png");
            JLabel l1 = new JLabel(image);
            l1.setBounds(90, 80, 300, 250);
            jFrame.add(l1);


            //layout of the generated weather information
            // day label
            dayLabel.setBounds(10, 360, 100, 50);
            jFrame.add(dayLabel);
            //day drop down menu
            day.setBounds(80, 360, 100, 50);
            jFrame.add(day);
            // hour label
            hourLabel.setBounds(180, 360, 100, 50);
            jFrame.add(hourLabel);
            //hour drop down menu
            hour.setBounds(250, 360, 100, 50);
            jFrame.add(hour);
            //back button
            back.setBounds(20, 20, 80, 30);
            BackButton backButton = new BackButton();
            back.addActionListener(backButton);
            jFrame.add(back);
            //okay button
            submitButton.setBounds(370, 370, 80, 30);
            submitButton.addActionListener(this); //used whenever the user wants to see the weather at a specific date and time
            jFrame.add(submitButton);
            //temp label info
            JLabel temperatureLabel = new JLabel();
            temperatureLabel.setText("Temperature");
            temperatureLabel.setBounds(220, 122, 200, 100);
            temperatureLabel.setFont(new Font("Verdana", Font.PLAIN, 11)); //setting the font type & size
            temperature.setBounds(210, 90, 200, 100);
            temperature.setFont(new Font("Verdana", Font.PLAIN, 45)); //setting the font type & size
            jFrame.add(temperature);
            jFrame.add(temperatureLabel);
            //sunrise and sunset
            sunrise.setBounds(140, 175, 200, 50);
            jFrame.add(sunrise);
            sunset.setBounds(275, 175, 200, 50);
            jFrame.add(sunset);
            //min temp and max temp
            minimumTemperature.setBounds(95, 240, 200, 50);
            jFrame.add(minimumTemperature);
            maximumTemperature.setBounds(275, 240, 200, 50);
            jFrame.add(maximumTemperature);
            ((Window) component).dispose();

        } catch (Exception exception) {
            error.setText("Try Again Later");
            error.setForeground(Color.RED);
        }
    }

    //action when the button back is clicked --> will go back to the first pop-up window
    private class BackButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            jFrame.dispose();
            component.setVisible(true);
        }
    }
}
