/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saoptimization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import java.util.Iterator;
import java.util.Random;



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

    public static ArrayList<Integer> p;

    public static BufferedReader reader;
    
    /*Variáveis para entrada do algoritmo*/
    public static int stop1, stop2;
    public static double temperatura, resfriamento;
    
    /*Constantes extras definidas para o algoritmo*/
    public static int VALOR_INFINITO = 99999;
    public static double CRITERIO = 0.1;
    
    /*Gerador de números aleatórios*/
    public static Random gerador_aleatorios;

    //public static File file1 = new File("ogapA332.txt");
    public static File file1 = new File("testeee.txt");
    public static File file2 = new File("ogapA1232.txt");
    public static File file3 = new File("ogapB331.txt");
    public static File file4 = new File("ogapB1131.txt");
    public static File file5 = new File("ogapC333.txt");
    public static File file6 = new File("ogapC1633.txt");
    public static File file7 = new File("opmed5.txt");
    public static File file8 = new File("opmed10.txt");
    public static File file9 = new File("opmed20.txt");
    public static File file10 = new File("opmed40.txt");
    
    //public static File infile1 = new File("332PM_GapA.txt");
    public static File infile1 = new File("inaaa.txt");
    public static File infile2 = new File("1232PM_GapA.txt");
    public static File infile3 = new File("331PM_GapB.txt");
    public static File infile4 = new File("1131PM_GapB.txt");
    public static File infile5 = new File("333PM_GapC.txt");
    public static File infile6 = new File("1633PM_GapC.txt");
    public static File infile7 = new File("pmed5.txt");
    public static File infile8 = new File("pmed10.txt");
    public static File infile9 = new File("pmed20.txt");
    public static File infile10 = new File("pmed40.txt");


    public static void main(String[] args) throws IOException
    {
        /*Le matriz de entrada*/
        inicializa_matriz_entrada(infile1);
        gera_arquivo_saida(file1);
        
        System.out.println("GEROU MATRIZ E ARQUIVO DE SAIDA");
        
        /*
        inicializa_matriz_entrada(infile2);
        gera_arquivo_saida(file2);
        inicializa_matriz_entrada(infile3);
        gera_arquivo_saida(file3);
        inicializa_matriz_entrada(infile4);
        gera_arquivo_saida(file4);
        inicializa_matriz_entrada(infile5);
        gera_arquivo_saida(file5);
        inicializa_matriz_entrada(infile6);
        gera_arquivo_saida(file6);
        inicializa_matriz_entrada(infile7);
        gera_arquivo_saida(file7);
        inicializa_matriz_entrada(infile8);
        gera_arquivo_saida(file8);
        inicializa_matriz_entrada(infile9);
        gera_arquivo_saida(file9);
        inicializa_matriz_entrada(infile10);
        gera_arquivo_saida(file10);
        */

        gerador_aleatorios = new Random();
        
        /*Simulated Annealing*/
        
        stop1 = 10;
        stop2 = 10;
        temperatura = 10.0;
        resfriamento = 0.4; //Valor [0,1]

        melhor_solucao = simulated_annealing(stop2, stop1, temperatura, resfriamento);
       
        //solucao_inicial();
    }

    public static void gera_arquivo_saida(File file) throws IOException
    {
        String concat="";
        String indices="";
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
	bw.write("data;\n");
        int i;
        concat = "set A := ";
        for(i=1; i<=dimension-1; i++){
            concat = concat + i + ", ";
            indices = indices + i + " ";
        }
        
        concat = concat + i + ";";
        bw.write(concat);
        indices = indices + i + ":=\n";
        
        bw.write("\n");
        bw.write("param p := " + facility_total + ";\n");
        
         bw.write("param dist: " + indices);
         
         
         int j;
         
         for(i=0; i<dimension-1; i++)
         {
             int temp = i+1;
             String linha ="";
             linha = linha + temp + "\t";
               for(j=0; j<dimension; j++)
               {
                   linha = linha + matriz_entrada[i][j] + " ";
               }
               bw.write(linha + "\n");
               //linha = "";
         }
         //ultima linha
         int temp = dimension - 1;
         String linha ="";
         linha = linha + dimension + "\t";
         for(j = 0; j<dimension -1; j++)
         {
             linha = linha + matriz_entrada[i][j] + " ";
         }
         linha = linha + matriz_entrada[i][j] +";";
         
         bw.write(linha);
         bw.write("\nend;");
         bw.close();
    }
    
    public static int[][] simulated_annealing(int stop2, int stop1, double temperatura, double resfriamento)
    {
        int melhor_valor_global = VALOR_INFINITO;
        int valor_perturbacao;
        int i,j;
       
        //1) Gera solução inicial
        melhor_solucao = solucao_inicial();
        
        System.out.println("ENCONTROU SOLUÇÃO INICIAL FACTÍVEL!!");
        
        
        
        //2) Obtem valor da solução inicial
        melhor_valor_global = funcao_avaliacao(melhor_solucao);
        
        System.out.println("JÁ CALCULOU FUNÇÃO DE AVALIAÇÃO!!");
        
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
                    if(probabilidade_de_saltos(valor_perturbacao, melhor_valor_global, temperatura)> Math.random()*(1-0)+0)
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
        /*
            Percorre matriz calculando o custo total
            se diagonal = 0, é uma facilidade
        */  
        int i, j;
        
        int[] distancias;

        distancias = new int[dimension];
       
        for(i=0; i<dimension; i++)
            distancias[i] = VALOR_INFINITO; //inicializa vetor de distâncias

        for(i=0; i<facility_total; i++)
        {
            int f = p.get(i);                       //seleciona facilidade

            for(j=0; j<dimension; j++)
                if(matriz[f][j] < distancias[j])    //verifica se distancia de i pra j é menor que a menor já encontrada
                    distancias[j] = matriz[f][j];   //se for, atualiza a menor distância pra j
        }

        int total = 0;

        for(i=0; i<dimension; i++)
            total += distancias[i];     //calcula a distancia total

        distancias = null;

        return total;
    }
    
    
    public static ArrayList<Integer> seleciona_p_medianas()
    {
        Random rand = new Random();
        int i;
        int facility;
        
       ArrayList <Integer> pmed = new ArrayList<Integer>();
        
        for(i=0; i<facility_total; i++)
        {
            facility = rand.nextInt(dimension);
            while(pmed.contains(facility))
            {
                facility = rand.nextInt(dimension);
            }
            
            pmed.add(facility);
        }               
        

            /*System.out.println(pmed.size());
            for (Integer item : pmed) 
            {   
                System.out.print(item + " ");
            }
        */
        
       
        
        return pmed;
    }
    
    public static int[][] solucao_inicial()
    {
        /*
        1) INICIALIZA MATRIZ COM VALORES INFINITOS
        2) VERIFICA SE ACHOU UMA SOLUÇÃO POSSÍVEL
        3) SE ACHOU, COLOCAR VALOR 0 NO ELEMENTO DA DIAGONAL CORRESPONETE A FACILIDADE
        */    
        //ArrayList<Integer> p = new ArrayList<Integer>();
                     
        int FALSE = 0;
        int TRUE = 1;
        int INFACTIVEL = -1;
        int FACTIVEL = 0;

        
        for(int i=0; i<dimension; i++){
             for(int j=0; j<dimension; j++)
            {
                    //System.out.print(matriz_entrada[i][j]+" ");
            }
             //System.out.print("\n");
         }  
         
          //System.out.println("MATRIZ[0][2] = " + matriz_entrada[0][2]);
        
        /////////////////////////////////////////////////
        //    INICIALIZA MATRIZ COM VALORES INFINITOS  //
        /////////////////////////////////////////////////
        //                                             //
        for(int i=0; i<dimension;i++)
            for(int j=0; j<dimension; j++)
                melhor_solucao[i][j] = VALOR_INFINITO;
        //                                             //
        /////////////////////////////////////////////////
              
        
        
        System.out.println("INCIALIZOU MATRIZ COM INFINITO");
        
        int sem_solucao = TRUE;
        while(sem_solucao == TRUE)
        {
            /*****************************************************/
            /*1) SELECIONA UM CONJUNTO ALEATÓRIO DE P FACILIDADES*/
            /*****************************************************/
            p = seleciona_p_medianas(); //Seleciona randomicamente as p facilidades
            
            //System.out.println("SELECIONOU AS P-MEDIANAS");
            /*************************************************/
            /*2) LIGA OS CLIENTES ÀS FACILIDADES MAIS PRÓXIMA*/
            /*************************************************/
            int cliente = 0;
            int solucao = FACTIVEL;            
            while(cliente<dimension && solucao == FACTIVEL)                        
            {                
                int melhor_distancia_atual = VALOR_INFINITO; 

                if(!p.contains(cliente)) //Só analisa nó se este não for uma facilidade
                {             
                    //System.out.println("Verificando cliente" + cliente);
                    //############################################################
                    //#Verifica a menor distância do cliente para cada facilidade#                  
                    //############################################################
                    Iterator<Integer> itr = p.iterator();
                    int cliente_antendido = FALSE;
                    while(itr.hasNext())
                    {
                        int facilidade = itr.next();//Seleciona uma facilidade e verifica a distancia até cliente         
                        //System.out.println("Matriz_entrada["+facilidade+"]"+"["+cliente+"]"+ " = " + matriz_entrada[facilidade][cliente]);
                        if(matriz_entrada[facilidade][cliente] < melhor_distancia_atual)//Se encontrou menor distância que a atual, atualiza
                        { //Se for melhor atualiza                        
                            melhor_solucao[facilidade][cliente] = matriz_entrada[facilidade][cliente];
                            //System.out.println("ACHOU LIGAÇÃO entre" + facilidade + " e " + cliente);
                            cliente_antendido = TRUE;
                        }                        
                    }
                    /*******************************/
                    /*3) DETECTA SOLUÇÃO INFACTIVEL*/
                    /*******************************/
                    if(cliente_antendido == FALSE)//Verifica se cliente foi ligado com alguma facilidade                                            
                        solucao = INFACTIVEL; //Indica que todo processo será repetido                                      
                }
                
                cliente++;
            }
            
            //System.out.println("VERIFICOU UM POSSÍVEL CENÁRIO");
            
            /*******************************************/
            /*4) DETECTOU UMA SOLUÇÃO FACTÍVEL POSSÍVEL*/
            /*******************************************/
            if(solucao == FACTIVEL)
            {
                System.out.println("ENCONTROU SOLUÇÃO FACTÍVEL");
                sem_solucao = FALSE;
            }
        }
        
        //////////////////////////////////////////////
        //    SETA ELEMENTO Xii DAS FACILIDADES = 0 //
        //////////////////////////////////////////////
        //                                          //
            for (Integer facilidade : p)
            {
                melhor_solucao[facilidade][facilidade] = 0;
            }
        //                                          //
        //////////////////////////////////////////////
        
            System.out.println("ATUALIZOU DIAGONAL DAS FACILIDADES");


        return melhor_solucao;
    }
    
    public static int[][] perturba_solucao(int[][] matriz)
    {
        int FACTIVEL = 0, INFACTIVEL = -1, FALSE = 0, TRUE = 1;

        int solucao = FACTIVEL;

        int cliente = 0;
        
        int[][] matriz_temp = new int[dimension][dimension];

  
        
        while(cliente<dimension && solucao == FACTIVEL)
        {
            for(int i=0; i<dimension;i++)
                for(int j=0; j<dimension;j++)
                    matriz_temp[i][j] = matriz[i][j];

              
            
            int entra_facilidade = gerador_aleatorios.nextInt(dimension);
            
            int sai_indice = gerador_aleatorios.nextInt(p.size());
          
            
            int sai_facilidade = p.get(sai_indice);
            
            p.remove(sai_facilidade);

            p.add(entra_facilidade);
                              
                int melhor_distancia_atual = VALOR_INFINITO;

                if(!p.contains(cliente)) //Só analisa nó se este não for uma facilidade
                {
                    //System.out.println("Verificando cliente" + cliente);
                    //############################################################
                    //#Verifica a menor distância do cliente para cada facilidade#
                    //############################################################
                    Iterator<Integer> itr = p.iterator();

   
                    
                    int cliente_antendido = FALSE;

                    while(itr.hasNext())
                    {
                        
                        int facilidade = itr.next();//Seleciona uma facilidade e verifica a distancia até cliente

                        if(matriz_entrada[facilidade][cliente] < melhor_distancia_atual)//Se encontrou menor distância que a atual, atualiza
                        { //Se for melhor atualiza
                            
                            matriz_temp[facilidade][cliente] = matriz_entrada[facilidade][cliente];
                            cliente_antendido = TRUE;
                                         
                        }
                    }                                     
                    /*******************************/
                    /*3) DETECTA SOLUÇÃO INFACTIVEL*/
                    /*******************************/
                    if(cliente_antendido == FALSE)//Verifica se cliente foi ligado com alguma facilidade
                    {               
                        solucao = INFACTIVEL; //Indica que todo processo será repetido
                        
                        p.remove(entra_facilidade);

                        p.add(sai_facilidade);
                           
                    }
                }
                

                cliente++;
        }

        
        for (Integer facilidade : p)
            matriz_temp[facilidade][facilidade] = 0;
                 
        return matriz_temp;
    }
    
    public static double probabilidade_de_saltos(int valor_candidato, int valor_global, double temperatura)
    {
        //Com probabilidade e^[−(f(s')−f(s))/kT]
        //-Lembro da professora ter falado sobe o K não fazer muita diferença e por isso poderíamos ignorar
        return Math.exp(-(valor_candidato - valor_global)/temperatura);
    }
    
    public static void inicializa_matriz_entrada(File file_path) throws FileNotFoundException, IOException
    {
        InputStream in = new FileInputStream(file_path);
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
               melhor_solucao = new int[dimension][dimension];
               solucao_candidata = new int[dimension][dimension];
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
               
               
               //System.out.println(dimension);                     
               
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
             {
                 //Coloca diagonal = 0
                 if(j==i)
                    matriz_entrada[i][j] = 0;
                 else
                    matriz_entrada[i][j] = VALOR_INFINITO;
             }         
         /*Enquanto não leu tudo do arquivo*/          
         while(reader.read() != -1)
         {
             /*Lê linha FACILIDADE-CLIENTE-CUSTO*/
             text = reader.readLine(); //Leu valor da dimensão e valor da mediana (p)
 
             tokens = text.split(" +"); //Separa os elementos da linha
             facilidade = Integer.parseInt(tokens[TIPO_ENTRADA]) - 1;
             cliente =  Integer.parseInt(tokens[TIPO_ENTRADA + 1]) - 1;
             custo = Integer.parseInt(tokens[TIPO_ENTRADA + 2]);
             
               //System.out.print(facilidade+1+" ");
               //System.out.print(cliente+1+" ");
               //System.out.print(custo);
               //System.out.println();             
             
             /*Atualiza informações na matriz*/
            if(TIPO_ENTRADA == 0)
            {
                matriz_entrada[facilidade][cliente] = custo;
                matriz_entrada[cliente][facilidade] = custo;
            }
            else if(TIPO_ENTRADA == 1)
            {
                matriz_entrada[facilidade][cliente] = custo;
            }                                   
            
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
    
