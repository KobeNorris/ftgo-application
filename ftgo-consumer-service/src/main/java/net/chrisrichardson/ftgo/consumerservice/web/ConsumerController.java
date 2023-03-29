package net.chrisrichardson.ftgo.consumerservice.web;

import com.google.gson.Gson;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import java.util.Map;
import net.chrisrichardson.ftgo.consumerservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CourierAvailability;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CreateOrderRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CreateOrderResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.DeliveryStatus;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetAccountResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetOrderResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetOrderResponseNew;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetOrdersResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetRestaurantResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetRestaurantResponseNew;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.ReviseOrderRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.TicketAcceptance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

  @Autowired RestTemplate restTemplate;

  private ConsumerService consumerService;

  private Logger logger = LoggerFactory.getLogger(getClass());

  private Gson gson = new Gson();

  public ConsumerController(ConsumerService consumerService) {
    this.consumerService = consumerService;
  }

  // @RequestMapping(method = RequestMethod.POST, path = "/consumers")
  // public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
  //   ResultWithEvents<Consumer> result = consumerService.create(request.getName());
  //   return new CreateConsumerResponse(result.result.getId());
  // }

    @RequestMapping(method = RequestMethod.POST, path = "/consumers")
  public CreateConsumerResponse create(@RequestBody Map<String, Object> request) {
    logger.info("path:/consumers | Received request body: " + gson.toJson(request));
    // ResultWithEvents<Consumer> result = consumerService.create(request.getName());
    ResultWithEvents<Consumer> result = consumerService.create(((CreateConsumerRequest) request).getName());
    return new CreateConsumerResponse(result.result.getId());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/consumers/{consumerId}")
  public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
    System.out.println("GET Called");
    return consumerService
        .findById(consumerId)
        .map(
            consumer ->
                new ResponseEntity<>(new GetConsumerResponse(consumer.getName()), HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(path = "account-service/accounts/{accountId}", method = RequestMethod.GET)
  public Object accountServiceGetAccount(@PathVariable long accountId) {
    System.out.println("GET Called");
    return restTemplate.getForObject("http://172.30.147.18:8085/accounts/" + accountId, Object.class);
  }

  // @RequestMapping(path = "consumer-service/consumers", method = RequestMethod.POST)
  // public CreateConsumerResponse consumerServicePostConsumer(
  //     @RequestBody CreateConsumerRequest request) {
  //   return restTemplate
  //       .postForEntity("http://172.30.147.18:8081/consumers", request, CreateConsumerResponse.class)
  //       .getBody();
  // }

  @RequestMapping(path = "consumer-service/consumers", method = RequestMethod.POST)
  public CreateConsumerResponse consumerServicePostConsumer(
      @RequestBody Map<String, Object> request)  {
    logger.info("path:consumer-service/consumers | Received request body: ", gson.toJson(request));
    return restTemplate
        .postForEntity("http://172.30.147.18:8081/consumers", (CreateConsumerRequest) request, CreateConsumerResponse.class)
        .getBody();
  }

  @RequestMapping(path = "consumer-service/consumers/{consumerId}", method = RequestMethod.GET)
  public Object consumerServiceGetConsumer(@PathVariable long consumerId) {
    System.out.println("GET Called");
    return restTemplate.getForObject(
        "http://172.30.147.18:8081/consumers/" + consumerId, Object.class);
  }

  // @RequestMapping(
  //     path = "delivery-service/couriers/{courierId}/availability",
  //     method = RequestMethod.POST)
  // public void deliveryServiceUpdateCourierLocation(
  //     @PathVariable long courierId, @RequestBody CourierAvailability availability) {
  //   restTemplate.postForEntity(
  //       "http://172.30.147.18:8089/couriers/" + courierId + "/availability",
  //       availability,
  //       Object.class);
  // }

  @RequestMapping(
      path = "delivery-service/couriers/{courierId}/availability",
      method = RequestMethod.POST)
  public void deliveryServiceUpdateCourierLocation(
      @PathVariable long courierId, Map<String, Object> request) {
    logger.info("path: delivery-service/couriers/{courierId}/availability | Received request body: ", gson.toJson((CourierAvailability) request));
    restTemplate.postForEntity(
      "http://172.30.147.18:8089/couriers/" + courierId + "/availability",
      (CourierAvailability) request,
      Object.class);
  }

  @RequestMapping(path = "delivery-service/deliveries/{deliveryId}", method = RequestMethod.GET)
  public Object deliveryServiceGetDeliveryStatus(@PathVariable long deliveryId) {
    System.out.println("GET Called");
    return restTemplate.getForObject(
        "http://172.30.147.18:8089/deliveries/" + deliveryId, Object.class);
  }

  // @RequestMapping(path = "kitchen-service/tickets/{ticketId}/accept", method = RequestMethod.POST)
  // public void acceptTicket(
  //     @PathVariable long ticketId, @RequestBody TicketAcceptance ticketAcceptance) {
  //   restTemplate.postForEntity(
  //       "http://172.30.147.18:8083/tickets/" + ticketId + "/accept", ticketAcceptance, Object.class);
  // }

  @RequestMapping(path = "kitchen-service/tickets/{ticketId}/accept", method = RequestMethod.POST)
  public void acceptTicket(
      @PathVariable long ticketId, @RequestBody TicketAcceptance request) {
    logger.info("path:kitchen-service/tickets/{ticketId}/accept | Received request body: ", gson.toJson(request));
    restTemplate.postForEntity(
        "http://172.30.147.18:8083/tickets/" + ticketId + "/accept", 
        (TicketAcceptance) request, Object.class);
  }

  @RequestMapping(path = "kitchen-service/restaurants/{restaurantId}", method = RequestMethod.GET)
  public Object getRestaurant(@PathVariable long restaurantId) {
    System.out.println("GET Called");
    return restTemplate.getForObject(
        "http://172.30.147.18:8083/restaurants" + restaurantId, Object.class);
  }

  @RequestMapping(path = "order-history-service/orders/", method = RequestMethod.GET)
  public Object orderHistoryServiceGetOrders(@RequestParam(name = "consumerId") String consumerId) {
    System.out.println("GET Called");
    return restTemplate.getForObject(
        "http://172.30.147.18:8086/orders?consumerId=" + consumerId, Object.class);
  }

  @RequestMapping(path = "order-history-service/orders/{orderId}", method = RequestMethod.GET)
  public Object orderHistoryServiceGetOrder(@PathVariable String orderId) {
    System.out.println("GET Called");
    return restTemplate.getForObject("http://172.30.147.18:8086/orders/" + orderId, Object.class);
  }

  // @RequestMapping(path = "order-service/orders", method = RequestMethod.POST)
  // public CreateOrderResponse orderServiceCreate(@RequestBody CreateOrderRequest request) {
  //   return restTemplate
  //       .postForEntity("http://172.30.147.18:8082/orders/", request, CreateOrderResponse.class)
  //       .getBody();
  // }

  @RequestMapping(path = "order-service/orders", method = RequestMethod.POST)
  public CreateOrderResponse orderServiceCreate(@RequestBody Map<String, Object> request) {
    logger.info("path:order-service/orders | Received request body: ", gson.toJson(request));
      return restTemplate
        .postForEntity("http://172.30.147.18:8082/orders/", 
        (CreateOrderRequest) request, CreateOrderResponse.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/orders/{orderId}", method = RequestMethod.GET)
  public Object orderServiceGetOrder(@PathVariable long orderId) {
    System.out.println("GET Called");
    return restTemplate.getForObject("http://172.30.147.18:8082/orders/" + orderId, Object.class);
  }

  @RequestMapping(path = "order-service/orders/{orderId}/cancel", method = RequestMethod.POST)
  public Object orderServiceCancel(@PathVariable long orderId) {
    System.out.println("POST Called");
    return restTemplate
        .postForEntity("http://172.30.147.18:8082/orders/" + orderId + "/cancel", null, Object.class)
        .getBody();
  }

  // @RequestMapping(path = "order-service/orders/{orderId}/revise", method = RequestMethod.POST)
  // public Object orderServiceRevise(
  //     @PathVariable long orderId, @RequestBody ReviseOrderRequest request) {
  //   return restTemplate
  //       .postForEntity(
  //           "http://172.30.147.18:8082/orders/" + orderId + "/revise", request, Object.class)
  //       .getBody();
  // }

  @RequestMapping(path = "order-service/orders/{orderId}/revise", method = RequestMethod.POST)
  public Object orderServiceRevise(
      @PathVariable long orderId, @RequestBody Map<String, Object> request) {
    logger.info("path:order-service/orders/{orderId}/revise Received request body: ", gson.toJson(request));
    return restTemplate
        .postForEntity(
            "http://172.30.147.18:8082/orders/" + orderId + "/revise", (ReviseOrderRequest) request, Object.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/restaurants/{restaurantId}", method = RequestMethod.POST)
  public Object orderServiceGetRestaurant(@PathVariable long restaurantId) {
    System.out.println("POST Called");
    return restTemplate.getForObject(
        "http://172.30.147.18:8082/restaurants/" + restaurantId, Object.class);
  }

  // @RequestMapping(path = "restaurant-service/restaurants", method = RequestMethod.POST)
  // public Object restaurantServiceCreate(@RequestBody CreateRestaurantRequest request) {
  //   return restTemplate
  //       .postForEntity("http://172.30.147.18:8084/restaurants/", request, Object.class)
  //       .getBody();
  // }

  @RequestMapping(path = "restaurant-service/restaurants", method = RequestMethod.POST)
  public Object restaurantServiceCreate(@RequestBody Map<String, Object> request) {
    logger.info("path:restaurant-service/restaurants | Received request body: ", gson.toJson(request));
    return restTemplate
        .postForEntity("http://172.30.147.18:8084/restaurants/", (CreateRestaurantRequest) request, Object.class)
        .getBody();
  }

  @RequestMapping(
      path = "restaurant-service/restaurants/{restaurantId}",
      method = RequestMethod.GET)
  public Object restaurantServiceGet(@PathVariable long restaurantId) {
    System.out.println("GET Called");
    return restTemplate.getForObject(
        "http://172.30.147.18:8084/restaurants/" + restaurantId, Object.class);
  }

  // public static String convert(HttpServletRequest request) {
  //   StringBuilder sb = new StringBuilder();
  //   try {
  //     InputStream inputStream = request.getInputStream();
  //     if (inputStream != null) {
  //       try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
  //         char[] charBuffer = new char[128];
  //         int bytesRead;
  //         while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
  //           sb.append(charBuffer, 0, bytesRead);
  //         }
  //       }
  //     }
  //   } catch (IOException e) {
  //     logger.warn("convert failed" + e.toString());
  //   }
  //   return sb.toString();
  // }
}
