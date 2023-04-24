
package com.crio.warmup.stock.portfolio;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

RestTemplate restTemplate;
StockQuotesService stockQuotesService;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService=stockQuotesService;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  






  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
       /*String url= buildUri(symbol, from, to);
        TiingoCandle ting[]=restTemplate.getForObject(url,TiingoCandle[].class);
        if(ting==null)
        return new ArrayList<Candle>();
     return Arrays.asList(ting);*/
     return stockQuotesService.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      // String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
        //    + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
            String url="https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate+"&endDate="+endDate+"&token="+"a0824d2f6a47b69a7d5fd01bde757b2cc32961b5";
            return url;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate)  {
    // TODO Auto-generated method stub
    List<AnnualizedReturn> list=new ArrayList<>();
    for(PortfolioTrade port:portfolioTrades)
    {
      AnnualizedReturn ret=calculateAnnualizedReturns(port, endDate);
      if(ret!=null)
      list.add(ret);  
    }
   /*return list.stream()
   .sorted((a1, a2) -> Double.compare(a2.getAnnualizedReturn(), a1.getAnnualizedReturn())) //descending order
   .collect(Collectors.toList());*/
   Collections.sort(list,getComparator());
   return list;
  }

  public  AnnualizedReturn calculateAnnualizedReturns(PortfolioTrade trade,LocalDate endDate)  {
        LocalDate currentDate =trade.getPurchaseDate();
        AnnualizedReturn res;
        List<Candle> ting;
        try {
          ting = getStockQuote(trade.getSymbol(),currentDate,endDate);
          if(ting!=null)
        {
        double numYears = currentDate.until(endDate, ChronoUnit.DAYS)/365.24;
        System.out.println(numYears);
        double buyPrice=ting.get(0).getOpen();
        double sellPrice=ting.get(ting.size()-1).getClose();
        double totalReturn=((sellPrice-buyPrice)*1.0)/buyPrice;
        double annualizedReturns=Math.pow((1+totalReturn),(1.0/numYears))-1;
        System.out.println("return="+annualizedReturns);
        res= new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturn);
        }
        else
        {
          res=null;
        }
        } catch (JsonProcessingException e) {
          // TODO Auto-generated catch block
          res=null;
        }
        
        return res;
  }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
