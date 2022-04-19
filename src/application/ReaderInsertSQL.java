package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import db.DB;
import db.DbException;

public class ReaderInsertSQL {

	public static void main(String[] args) {
		Connection conn = null;
		Statement st = null;
		Integer i = 0;

		try {
			conn = DB.getConnection();

			conn.setAutoCommit(false);

			st = conn.createStatement();
			
			Properties props = DB.loadProperties();
			String pathSource = props.getProperty("pathSource");
			

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(pathSource), "UTF8"))) {
				String line = br.readLine();
				line = br.readLine();

				while (line != null) {

					String[] fields = line.split(":");

					if (fields.length >= 12) {

						String phone = fields[0];
						phone = StringEscapeUtils.escapeEcmaScript(phone);
						phone = StringUtils.abbreviate(phone, 20);

						String number = fields[1];
						number = StringEscapeUtils.escapeEcmaScript(number);
						number = StringUtils.abbreviate(number, 20);

						String name = fields[2];
						name = StringEscapeUtils.escapeEcmaScript(name);
						name = StringUtils.abbreviate(name, 60);

						String secondName = fields[3];
						secondName = StringEscapeUtils.escapeEcmaScript(secondName);
						secondName = StringUtils.abbreviate(secondName, 100);

						String gender = fields[4];
						gender = StringEscapeUtils.escapeEcmaScript(gender);
						gender = StringUtils.abbreviate(gender, 11);

						String city = fields[5];
						city = StringEscapeUtils.escapeEcmaScript(city);
						city = StringUtils.abbreviate(city, 100);

						String hometown = fields[6];
						hometown = StringEscapeUtils.escapeEcmaScript(hometown);
						hometown = StringUtils.abbreviate(hometown, 100);

						String civilStatus = fields[7];
						civilStatus = StringEscapeUtils.escapeEcmaScript(civilStatus);
						civilStatus = StringUtils.abbreviate(civilStatus, 60);

						String workplace = fields[8];
						workplace = StringEscapeUtils.escapeEcmaScript(workplace);
						workplace = StringUtils.abbreviate(workplace, 100);

						Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(line);

						if (m.find()) {
							String email = m.group();
							User user = new User(name, secondName, phone, number, gender, city, hometown, civilStatus,
									workplace, email);

							st.execute(
									"INSERT INTO users (Name, LastName, Phone, Number, Gender, City, Hometown, StatusCivil, Occupation, Email) VALUES ('"
											+ user.getName() + "', '" + user.getSecondName() + "', '" + user.getPhone()
											+ "', '" + user.getNumber() + "', '" + user.getGender() + "', '"
											+ user.getCity() + "', '" + user.getHometown() + "', '"
											+ user.getCivilStatus() + "', '" + user.getWorkplace() + "', '"
											+ user.getEmail() + "');");

							line = br.readLine();
							i++;

						} else {

							User user = new User(name, secondName, phone, number, gender, city, hometown, civilStatus,
									workplace, "");

							st.execute(
									"INSERT INTO users (Name, LastName, Phone, Number, Gender, City, Hometown, StatusCivil, Occupation, Email) VALUES ('"
											+ user.getName() + "', '" + user.getSecondName() + "', '" + user.getPhone()
											+ "', '" + user.getNumber() + "', '" + user.getGender() + "', '"
											+ user.getCity() + "', '" + user.getHometown() + "', '"
											+ user.getCivilStatus() + "', '" + user.getWorkplace() + "', '"
											+ user.getEmail() + "');");

							line = br.readLine();
							i++;
						}
					} else {
						System.out.println("WARNING, LINE NOT INSERTED: " + line);

						line = br.readLine();
						i++;
					}

				}
				
				System.out.println("Created file");


			} catch (IOException e) {
				System.out.println("Error writing file: " + e.getMessage());
			}


			conn.commit();

		} catch (SQLException e) {
			try {
				conn.rollback();
				System.out.println(i);
				throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
			} catch (SQLException e1) {
				throw new DbException("Error trying to rolleback! Caused by: " + e1.getMessage());
			}
		} finally {
			DB.closeStatement(st);
			DB.closeConnection();
		}
	}
}
