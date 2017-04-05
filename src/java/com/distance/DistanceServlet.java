/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distance;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nail yusupov
 */
public class DistanceServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        if (request.getParameter("zip1") != null && request.getParameter("zip2") != null) {
            
            try{
            String type = request.getParameter("type") == null ? "mi" : request.getParameter("type");

            URL url = new URL("http://api.geonames.org/postalCodeLookupJSON?postalcode=" + request.getParameter("zip1") + "&country=US&username=nailyusupov");
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = br.readLine();
            JsonElement jelement = new JsonParser().parse(line);
            JsonObject jobject = jelement.getAsJsonObject();
            jobject = jobject.getAsJsonArray("postalcodes").get(0).getAsJsonObject();
            String lng1 = jobject.get("lng").toString();
            String lat1 = jobject.get("lat").toString();

            url = new URL("http://api.geonames.org/postalCodeLookupJSON?postalcode=" + request.getParameter("zip2") + "&country=US&username=nailyusupov");
            conn = url.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            line = br.readLine();
            jelement = new JsonParser().parse(line);
            jobject = jelement.getAsJsonObject();
            jobject = jobject.getAsJsonArray("postalcodes").get(0).getAsJsonObject();
            String lng2 = jobject.get("lng").toString();
            String lat2 = jobject.get("lat").toString();
            br.close();

            response.getWriter().write(new Gson().toJson(String.format("%.2f",haversine(Double.valueOf(lat1), Double.valueOf(lng1), Double.valueOf(lat2), Double.valueOf(lng2), type.equals("km") ? 6371 : 3959))));
            }catch(Exception e){
                response.getWriter().write(new Gson().toJson("Invalid parameters"));
            }
        } else {
            response.getWriter().write(new Gson().toJson("/distance?zip1=xxxxx&zip2=xxxxx\n\ndistance between zip codes is calculated in miles use type=km for kilometers"));
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public static double haversine(
            double lat1, double lng1, double lat2, double lng2, int r) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }

}
