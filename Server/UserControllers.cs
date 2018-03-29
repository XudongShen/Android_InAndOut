using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace WebApplication6.Controllers
{

    public class UserController : ApiController
    {
        public string GetRead(string title)
        {
            string filepath = "C:\\Web\\" + title + ".txt";
            if (File.Exists(filepath))
            {
                StreamReader sr = new StreamReader(filepath);
                string result = sr.ReadLine();
                sr.Close();
                return result;
            }
            return "";
        }

        public string GetSave(string title, string content)
        {
            string filepath = "C:\\Web\\" + title + ".txt";
            if (File.Exists(filepath))
            {
                File.Delete(filepath);
            }
            StreamWriter sw = new StreamWriter(filepath);
            sw.WriteLine(content);
            sw.Close();
            return title + ".txt saved success";
        }
    }

}
