package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();
        ModelAndView mv = new ModelAndView();
        if (ex instanceof NoLoginException){
            mv.setViewName("redirect:/index");
            return mv;
        }


        mv.setViewName("error");
        mv.addObject("code",400);
        mv.addObject("msg","系统异常，请稍后再试...");

        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if (null == responseBody){
                if (ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    mv.addObject("code",pe.getCode());
                    mv.addObject("msg",pe.getMsg());
                }
                return mv;
            }else {
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试！");
                if (ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                response.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out = null;

                try {
                    out = response.getWriter();
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }
        return mv;
    }
}
