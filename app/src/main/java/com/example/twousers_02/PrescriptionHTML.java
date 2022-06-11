package com.example.twousers_02;

public class PrescriptionHTML {

    public static String getHeadHTML() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='utf-8'>"+
                "<link href='https://fonts.googleapis.com/css?family=Allerta Stencil' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=Berkshire Swash' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=Aldrich' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=Baumans' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=Cantata One' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=EB Garamond' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=Cabin' rel='stylesheet'>" +
                "<link href='https://fonts.googleapis.com/css?family=Righteous' rel='stylesheet'>" +
                "<style>" +
                ".drname{" +
                "font-family: 'Berkshire Swash';" +
                "font-size: 30px;" +
                "}" +
                ".infodr{" +
                "font-family: 'EB Garamond';" +
                "font-weight: bold;" +
                "font-size: 15px;" +
                "}" +
                ".big_pat{" +
                "font-size: 25px;" +
                "}" +
                ".small_pat{" +
                "font-size: 15px;" +
                "font-weight: lighter;" +
                "}" +
                ".big_pat, .small_pat{" +
                "font-family: 'Allerta Stencil';" +
                "padding-left: 7%;" +
                "}" +
                ".Drug, .INN{" +
                "font-family: 'Aldrich';" +
                "}" +
                ".Drug{" +
                "font-size: 20px;" +
                "}" +
                ".INN{" +
                "font-size: 15px;" +
                "font-weight: lighter;" +
                "font-style: italic;" +
                "}" +
                "th{" +
                "font-family: 'Cantata One';" +
                "font-size: 15px;" +
                "}" +
                ".Freq, .CMA, .Dur, .Gap, .Qty, .ROA, .Cond, .Fast{" +
                "font-family: 'Baumans';" +
                "font-size: 15px;" +
                "}" +
                ".diaginfo" +
                "{" +
                "font-family: 'Cabin';" +
                "font-size: 20px;" +
                "}" +
                ".name" +
                "{" +
                "font-family: Righteous;" +
                "}" +
                "</style>" +
                "</head>"+
                "<body>";
    }

    public static String getDocInfo(String[] doc_info){
        return  "<table style=\"border: 0px;width: 100%;align-self: center;\">" +
                "<tr>" +
                "<td rowspan=\"4\" width=\"10%\"><center><img src=\"https://firebasestorage.googleapis.com/v0/b/testing-modules-27d9b.appspot.com/o/Caduceus.svg?alt=media&token=6b501ceb-216d-4af8-a8b8-c47538cf367f\" height=100px></center></td>" +
                "<td class=\"drname\" width=\"70%\">Dr. " + doc_info[0] + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"infodr\">" + doc_info[1] + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"infodr\">Registration No: " + doc_info[2] + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"infodr\">" + doc_info[3] + "</td>" +
                "</tr>" +
                "</table><hr color=\"#000080\" width=\"85%\" style=\"border-radius: 100%\">";
    }

    public static String getPatInfo(String[] pat_info, String date, String prescription_no){
        return "<table style=\"width: 100%;align-self: center;border: 1px\">" +
                "<tr>" +
                "<td class=\"big_pat\">"+pat_info[0]+"</td>" +
                "<td class=\"big_pat\">"+date+"</td>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"small_pat\">"+pat_info[1]+" "+pat_info[2]+"</td>" +
                "<td class=\"small_pat\">#"+prescription_no+"</td>" +
                "</tr>"+
                "</table>";
    }

    public static String getPresHTML(String PresHTML, String[] prescribe){
        return PresHTML +
                "<tr>" +
                "<td>" +
                "<div class=\"Drug\">"+prescribe[0]+"</div>" +
                "<div class=\"INN\">"+prescribe[1]+"</div>" +
                "</td>" +
                "<td>" +
                "<center>" +
                "<div class=\"Freq\">"+prescribe[2]+"</div>" +
                "<hr color=#000080 width=\"70%\">" +
                "<div class=\"CMA\">"+prescribe[3]+"</div>" +
                "</center>" +
                "</td>" +
                "<td>" +
                "<center>" +
                "<div class=\"Dur\">"+prescribe[4]+"</div>" +
                "<hr color=#000080 width=\"70%\">" +
                "<div class=\"Gap\">"+prescribe[5]+"</div>" +
                "</center>" +
                "</td>" +
                "<td>" +
                "<center>" +
                "<div class=\"Qty\">"+prescribe[6]+"</div>" +
                "<hr color=#000080 width=\"70%\">" +
                "<div class=\"ROA\">"+prescribe[7]+"</div>" +
                "</center>" +
                "</td>" +
                "<td>" +
                "<center>" +
                "<div class=\"Cond\">"+prescribe[8]+"</div>" +
                "<hr color=#000080 width=\"70%\">" +
                "<div class=\"Fast\">"+prescribe[9]+"</div>" +
                "</center>" +
                "</td>" +
                "</tr>";
    }

    public static String getPresHead(){
        return "<br><br>" +
                "<table border=\"1px\" style=\"border-color: navy;\" width=90% align=\"center\">" +
                "<tr>" +
                "<th>Prescription</th>" +
                "<th>Frequency/Rhythm</th>" +
                "<th>Duration</th>" +
                "<th>Quantity</th>" +
                "<th>Condition</th>" +
                "</tr>";
    }

    public static String getFooterHTML(String name, String URL){
        return "<br><br><br><br><br><br><br>" +
                "<div align=\"right\" style=\"padding-right: 5%;\"><img src=\""+URL+"\" style=\"height: 100px;\"></div>" +
                "<div align=\"right\" style=\"padding-right: 5%;\">Dr. "+ name +"</div>" +
                "<footer><center>Made using <span class=\"name\">Prescribe.ME</span></center></footer>" +
                "</body>" +
                "</html>";
        //https://static.cdn.wisestamp.com/wp-content/uploads/2020/08/Oprah-Winfrey-Signature-1.png
    }

    public static String getDiagInfo(String title, String message){
        return "<br><br>" +
                "<div class=\"diaginfo\">" +
                "<b>" + title + ":</b> " +
                message + "</div>";
    }

    public static String generatePrescriptionNo() {
        String pres_no="";
        String alphabets="ABCDEFGHIJKLMNOPQRSTUVWXYZ", numbers="0123456789";
        for(int i=0; i<8; i++)
            if(i>1 && i<5)
                pres_no=pres_no + numbers.charAt((int) ((int) 0 + 10*Math.random()));
            else
                pres_no=pres_no + alphabets.charAt((int) ((int) 0 + 26*Math.random()));
        return pres_no;
    }
}
