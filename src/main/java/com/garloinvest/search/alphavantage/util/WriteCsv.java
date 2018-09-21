package com.garloinvest.search.alphavantage.util;

import com.garloinvest.search.alphavantage.dto.AlphavantageQuotation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * A temperately Database
 */
@Repository
public class WriteCsv {
    private static final Logger LOG = LoggerFactory.getLogger(WriteCsv.class);
    private static final String DELIMITER = ",";
    private static final String NEW_LINE = "\n";
    private static final String HEADER = "date,open,high,low,close,volume";



    public void savedAlphavantageTimeSeriesIntraday(Map<String, AlphavantageQuotation> quoteMap, String lastRefreshed) {

        String lastDate = formatDate(lastRefreshed);
        File file = new File("Quote"+lastDate+".csv");

        if(file.exists()) {
            return;
        }

        LOG.info("Creating a new Quote file");

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter("Quote"+lastDate+".csv"));
            StringBuilder sb = new StringBuilder();
            sb.append(HEADER);
            sb.append(NEW_LINE);
            for(Map.Entry<String, AlphavantageQuotation> entry : quoteMap.entrySet()) {
                AlphavantageQuotation quotation = entry.getValue();
                sb.append(entry.getKey()); //date
                sb.append(DELIMITER);
                sb.append(quotation.getOpen());
                sb.append(DELIMITER);
                sb.append(quotation.getHigh());
                sb.append(DELIMITER);
                sb.append(quotation.getLow());
                sb.append(DELIMITER);
                sb.append(quotation.getClose());
                sb.append(DELIMITER);
                sb.append(quotation.getVolume());
                sb.append(NEW_LINE);

            }
            printWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error parsing a new Quote file: {}",e.getMessage());
        } finally {
            printWriter.close();
        }
    }

    private String formatDate(String lastRefreshed) {
        StringBuilder date = new StringBuilder(StringUtils.replace(lastRefreshed," ","-"));
        return StringUtils.replace(date.toString(),":","-").toString();
    }

}
