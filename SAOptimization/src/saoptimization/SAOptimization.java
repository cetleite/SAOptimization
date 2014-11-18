/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saoptimization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author carloseduardo
 */
public class SAOptimization {

    /**
     * @param args the command line arguments
     */
    
    public static InputStream is;
    public static int dimension;
    public static int facility_total;
    public static String delims = "[ ]+";
    public static String[] tokens;
    public static String text;
    //public static  SimpleMatrix matriz_entrada;
    public static int[][] matriz_entrada;
    public static BufferedReader reader;

    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        /*Le matriz de entrada*/
        inicializa_matriz_entrada();
        
        /*Simulated Annealing*/
        simulated_annealing();
       
    }
    
    public static void simulated_annealing()
    {
        int melhor_valor_global;
        
        
    }
    
    public static void inicializa_matriz_entrada() throws FileNotFoundException, IOException
    {
        
        
        InputStream in = new FileInputStream(new File("in.txt"));
        reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
                
           text = reader.readLine();
           if(text.contains("C")) //Já pulou Code...
           {
               reader.readLine(); //Pula " Dimension, Median"
               
               /*Lê linha que contém dimensão e mediana da matriz*/
               text = reader.readLine(); //Leu valor da dimensão e valor da mediana (p)
               tokens = text.split(" +"); //Separa os elementos da linha
              
               /*Atualiza valor da dimensão da matriz*/
               dimension = Integer.parseInt(tokens[1]);
               /*Atualiza valor da mediana (p)*/
               facility_total = Integer.parseInt(tokens[2]);
               /*Inicializa matriz com a dimensão obtida*/
               matriz_entrada = new int[dimension][dimension];
               reader.readLine(); //Pula " Facility, Client, Transportation Cost"
               
               /*Executa a leitura dos elementos da matriz*/
               inicializa_elementos_da_matriz(1);                                                                                  
           
           }
           else //Já leu valor da dimensão, número de ligações e valor da mediana (p)              
           {                
               /*Separa os elementos lidos (dimensão da matriz número de ligações e valor da mediana (p) */
               tokens = text.split(" +"); //Separa os elementos da linha
              
               /*Atualiza valor da dimensão da matriz*/
               dimension = Integer.parseInt(tokens[1]);
               /*Atualiza valor da mediana (p)*/
               facility_total = Integer.parseInt(tokens[3]); //Aqui é 3, pois no arquivo tipo 2 o p-mediana é dado na terceira coluna
               /*Inicializa matriz com a dimensão obtida*/
               matriz_entrada = new int[dimension][dimension];
               
               
               System.out.println(dimension);                     
               
               /*Executa a leitura dos elementos da matriz*/
               inicializa_elementos_da_matriz(0);
           }          

        reader.close();
    }
    
    public static void inicializa_elementos_da_matriz(int TIPO_ENTRADA) throws IOException
    {
         int facilidade;
         int cliente;
         int custo;
         /*Enquanto não leu tudo do arquivo*/          
         while(reader.read() != -1)
         {
             /*Lê linha FACILIDADE-CLIENTE-CUSTO*/
             text = reader.readLine(); //Leu valor da dimensão e valor da mediana (p)
 
             tokens = text.split(" +"); //Separa os elementos da linha
             facilidade = Integer.parseInt(tokens[TIPO_ENTRADA]) - 1;
             cliente =  Integer.parseInt(tokens[TIPO_ENTRADA + 1]) - 1;
             custo = Integer.parseInt(tokens[TIPO_ENTRADA + 2]);
             
             //System.out.print(facilidade + "");
             
             /*Atualiza informações na matriz*/
            matriz_entrada[facilidade][cliente] = custo;
            matriz_entrada[cliente][facilidade] = custo;
            
            
         }
         
         int i,j;
         for(i=0; i<dimension; i++){
             for(j=0; j<dimension; j++)
            {
                System.out.print(matriz_entrada[i][j]+" ");
            }
             System.out.print("\n");
             
         }
                 
        
        
    }
}
    

