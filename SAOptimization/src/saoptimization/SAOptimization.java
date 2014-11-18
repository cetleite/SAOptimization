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
import java.util.Arrays;

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
    
    /*Matrizes a serem utilizadas para solução*/
    public static int[][] matriz_entrada;    
    public static int[][] melhor_solucao;
    public static int[][] solucao_candidata;
    
    public static BufferedReader reader;
    
    /*Variáveis para entrada do algoritmo*/
    public static int stop1, stop2;
    public static double temperatura, resfriamento;
    
    /*Constantes extras definidas para o algoritmo*/
    public static int VALOR_INFINITO = 9999;
    public static double CRITERIO = 0.1;
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        /*Le matriz de entrada*/
        inicializa_matriz_entrada();
        
        /*Simulated Annealing*/
        
        stop1 = 10;
        stop2 = 10;
        temperatura = 10.0;
        resfriamento = 0.4; //Valor [0,1]
        
        
        melhor_solucao= simulated_annealing(stop2, stop1, temperatura, resfriamento);
       
    }
    
    public static int[][] simulated_annealing(int stop2, int stop1, double temperatura, double resfriamento)
    {
        int melhor_valor_global = VALOR_INFINITO;
        int valor_perturbacao;
        int i,j;
        
        //1) Gera solução inicial
        melhor_solucao = solucao_inicial(matriz_entrada);
        //2) Obtem valor da solução inicial
        melhor_valor_global = funcao_avaliacao(melhor_solucao);
        
        /*Inicia laço do algoritmo*/
        //////////////////////
        //repeat STOP2 vezes//
        //////////////////////
        for(i=0; i<stop2; i++)
        {
            //////////////////////
            //repeat STOP1 vezes//
            //////////////////////
            for(j=0; j<stop1; j++)
            {
                //3) Perturba melhor solução atual      
                
                ///////////////////////////////////////////////////
                //seleciona s '∈ N (s) que ainda não foi visitado//
                ///////////////////////////////////////////////////
                solucao_candidata = perturba_solucao(melhor_solucao);
                //4) Avalia valor da solução perturbada
                valor_perturbacao = funcao_avaliacao(solucao_candidata);
                //5) Verifica se perturbação gerou resultado melhor
                
                ////////////////////////
                //if f(s') ≤ f(s) then//
                ////////////////////////
                if(valor_perturbacao >= melhor_valor_global)
                {
                    //////////
                    //s:= s'//
                    //////////
                    
                    //Se gerou, atualiza solução global atual
                    melhor_solucao = solucao_candidata;
                    melhor_valor_global =  valor_perturbacao;
                }
                //6) Se não for melhor, analisa a probabilidade e ve se muda ou nao
                else
                {
                    ////////////////////////////////////////////////////
                    //Com probabilidade e^[−(f(s')−f(s))/kT] : s := s'//
                    ////////////////////////////////////////////////////

                    //7) Se probabilidade estiver dentro de algum critério, alterar solução mesmo não sendo a melhor
                    if(probabilidade_de_saltos(valor_perturbacao, melhor_valor_global, temperatura)> CRITERIO)
                    {                        
                      melhor_solucao = solucao_candidata;
                      melhor_valor_global = valor_perturbacao;
                    }    
                }
            }
            //////////////
            //T := T × r//
            //////////////
            temperatura = temperatura * resfriamento;
        }
        
        return melhor_solucao;
        
    }
    
    public static int funcao_avaliacao(int[][] matriz)
    {
        //Ainda não implementado
        return 0;
    }
    
    public static int[][] solucao_inicial(int[][] matriz)
    {
        //Ainda não implementado  
        return matriz;
    }
    
    public static int[][] perturba_solucao(int[][] matriz)
    {
        //Ainda não implementado        
        
        return matriz;
    }
    
    public static double probabilidade_de_saltos(int valor_candidato, int valor_global, double temperatura)
    {
        //Com probabilidade e^[−(f(s')−f(s))/kT]
        //-Lembro da professora ter falado sobe o K não fazer muita diferença e por isso poderíamos ignorar
        return Math.exp(-(valor_candidato - valor_global)/temperatura);
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
         
         int i,j;
         
         for(i=0;i<dimension;i++)
             for(j=0; j<dimension;j++)            
                 matriz_entrada[i][j] = VALOR_INFINITO;
         
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
         
         for(i=0; i<dimension; i++){
             for(j=0; j<dimension; j++)
            {
                System.out.print(matriz_entrada[i][j]+" ");
            }
             System.out.print("\n");
             
         }
    }
}
    

