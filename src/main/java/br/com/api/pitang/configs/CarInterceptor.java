package br.com.api.pitang.configs;

import br.com.api.pitang.services.CarService;
import br.com.api.pitang.services.UserService;
import static java.lang.Long.parseLong;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class CarInterceptor implements HandlerInterceptor {

    private final CarService carService;
    private final UserService userService;

    public CarInterceptor(CarService carService, UserService userService) {
        this.carService = carService;
        this.userService = userService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String pathInfo = request.getRequestURI();
        System.out.println(pathInfo);
        String[] parts = pathInfo.split("/");
        Long carId = parseLong(parts[parts.length - 1]);
        carService.updateUsageCounter(carId);
        userService.updateTotalUsageCounter();
    }
}
