package de.easytory.gui;
import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;


/*
Easytory - the easy repository
Copyright (C) 2012, Ralf Konwalinka

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
public class AboutDialog extends JDialog 
{

private static final long serialVersionUID = 1L;

final private String licence= "<html><div style=\"font-family:Arial,sans-serif;color:222222;font-size:9px\"><b>Easytory - the easy repository</b><br><br>\n\n"
+ "Copyright (C) 2012, Ralf Konwalinka<br><br>\n\n"
+ "This program is free software: you can redistribute it and/or modify "
+ "it under the terms of the GNU General Public License as published by "
+ "the Free Software Foundation, either version 3 of the License, or "
+ "(at your option) any later version.<br><br>\n\n"
+ "This program is distributed in the hope that it will be useful, "
+ "but WITHOUT ANY WARRANTY; without even the implied warranty of "
+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the "
+ "GNU General Public License for more details.<br><br>\n\n"
+ "You should have received a copy of the GNU General Public License "
+ "along with this program. If not, see <a href=\"http://www.gnu.org/licenses/\">http://www.gnu.org/licenses/</a><br><br>\n\n"
+ "This software contains unmodified binary redistributions for H2 database engine (http://www.h2database.com/), "
+ "which is dual licensed and available under a modified version of the MPL 1.1 (Mozilla Public License) "
+ "or under the (unmodified) EPL 1.0 (Eclipse Public License). "
+ "An original copy of the license agreement can be found at: <a href=\"http://www.h2database.com/html/license.html\">http://www.h2database.com/html/license.html<br></a>\n"
+ "</div></html>";


public AboutDialog(JFrame frame) 
{
    super(frame, true);
    //JLabel label = new JLabel(licence);
    JTextPane text = new JTextPane();
    text.setContentType("text/html");
    text.setEditable(false); 
    text.setText(licence);
    //text.setFont(label.getFont().deriveFont(Font.PLAIN)); // non bold font
    getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);
    //getContentPane().add(statusArea, BorderLayout.SOUTH);
}


}
