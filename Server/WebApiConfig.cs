using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Web.Http;
using Microsoft.Owin.Security.OAuth;

namespace WebApplication6
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API ���úͷ���
            // �� Web API ����Ϊ��ʹ�ò��������������֤��
            config.SuppressDefaultHostAuthentication();
            config.Filters.Add(new HostAuthenticationFilter(OAuthDefaults.AuthenticationType));

            // Web API ·��
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{action}/{title}",
                defaults: new { title = RouteParameter.Optional }
            );

            config.Routes.MapHttpRoute(
                name: "Default1Api",
                routeTemplate: "api/{controller}/{action}/{title}/{content}",
                defaults: new { title = "", content = "" }
            );
        }
    }
}
