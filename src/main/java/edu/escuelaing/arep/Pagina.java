package edu.escuelaing.arep;

@Componente
public class Pagina {
    

    @GetMapping("/pagina/index")
    public static String getpagina(String a){
        return "<html><body><h1>Bienvenido a la primera pagina</h1> <br/><center><img src=\"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRtGVgv2jFPy-7QZLbcHatatOw7QJlcZjZ3Ig&usqp=CAU \"</center></body></html>";
    }
    @GetMapping("/pagina/login")
    public static String getlog(String a){
        return "<html><body><h2>Login To Your Account</h2><form class=\"loginbox\" autocomplete=\"off\"><input placeholder=\"Username\" type=\"text\" id=\"username\"/> <input placeholder=\"Password\" type=\"password\" id=\"password\"/><button id=\"submit\">Login</button></form></body></html>";
    }
}
