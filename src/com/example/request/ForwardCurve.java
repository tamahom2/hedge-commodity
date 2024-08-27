package com.example.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class ForwardCurve {

    public static class OptionData {
        String symbol;
        public String expiration;
        public double close;
        int priceScale;
        int minMove;

        public OptionData(String symbol, String expiration, double close, int priceScale, int minMove) {
            this.symbol = symbol;
            this.expiration = expiration;
            this.close = close;
            this.priceScale = priceScale;
            this.minMove = minMove;
        }
        
        public String toString() {
        	return symbol + " will expire " + expiration + " at the price " + close;
        }
    }

    public static List<OptionData> forwardRequest(String ticker) throws IOException {
        // URL
        String url = "https://scanner.tradingview.com/futures/scan";

        // Headers
        String jsonPayload = new JSONObject()
            .put("filter", new JSONArray()
                .put(new JSONObject().put("left", "expiration").put("operation", "nempty"))
                .put(new JSONObject().put("left", "subtype").put("operation", "nequal").put("right", "continuous"))
            )
            .put("index_filters", new JSONArray()
                .put(new JSONObject().put("name", "root").put("values", new JSONArray().put(ticker)))
            )
            .put("markets", new JSONArray().put("futures"))
            .put("columns", new JSONArray().put("expiration").put("close").put("pricescale").put("minmov"))
            .put("sort", new JSONObject().put("sortBy", "expiration").put("sortOrder", "asc"))
            .toString();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("accept", "*/*");
        httpPost.setHeader("accept-language", "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7,ar;q=0.6");
        httpPost.setHeader("content-type", "text/plain;charset=UTF-8");
        httpPost.setHeader("origin", "https://www.tradingview.com");
        httpPost.setHeader("referer", "https://www.tradingview.com/");
        httpPost.setHeader("sec-ch-ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPost.setHeader("sec-fetch-dest", "empty");
        httpPost.setHeader("sec-fetch-mode", "cors");
        httpPost.setHeader("sec-fetch-site", "same-site");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");

        try {
            httpPost.setEntity(new StringEntity(jsonPayload));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpEntity entity = client.execute(httpPost).getEntity();
        String responseString = EntityUtils.toString(entity);
        client.close();

        JSONObject responseJson = new JSONObject(responseString);
        JSONArray dataArray = responseJson.getJSONArray("data");

        List<OptionData> optionDataList = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            String symbol = item.getString("s");
            JSONArray data = item.getJSONArray("d");
            String expiration = Integer.toString(data.getInt(0));
            double close = data.getDouble(1);
            int priceScale = data.getInt(2);
            int minMove = data.getInt(3);
            optionDataList.add(new OptionData(symbol, expiration, close, priceScale, minMove));
        }

        return optionDataList;
    }
    
    public static void plotForwardCurve(List<OptionData> options) {
        TimeSeries series = new TimeSeries("Forward Curve");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        for (OptionData option : options) {
            try {
                series.add(new Day(dateFormat.parse(option.expiration)), option.close);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Forward Curve", "Expiration Date", "Price", dataset, false, true, false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

        ApplicationFrame frame = new ApplicationFrame("Forward Curve");
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) throws IOException {
    	List<OptionData> options = forwardRequest("NYMEX:CL");
    	for(OptionData op : options) {
    		System.out.println(op);
    	}
    	plotForwardCurve(options);
    }
}
