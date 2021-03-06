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

    /*CONSTANTES*/
    public static final int FALSE = 0;
    public static final int TRUE = 1;
    
    public static final int INFACTIVEL = -1;
    public static final int FACTIVEL = 0;
    
    public static final int CUSTO = 2;
    public static final int FACILIDADE = 1;
    public static final int NO = 0;
    
    public static final int VALOR_INFINITO = 101;
    public static final double CRITERIO = 0.1;
    
    /*ATRIBUTOS*/    
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
    
    public static int [][] solucao_atual;
    public static int [][] solucao_candidata2;    

    public static ArrayList<Integer> p;
    public static ArrayList <Integer> pmed;   

    public static BufferedReader reader;
    
    /*Variáveis para entrada do algoritmo*/
    public static int stop1, stop2;
    public static double temperatura, resfriamento;
    
    /*Gerador de números aleatórios*/
    public static Random gerador_aleatorios;

    public static File file1 = new File("ogapA332.txt");
    //public static File file1 = new File("testeee.txt");
    public static File file2 = new File("ogapA1232.txt");
    public static File file3 = new File("ogapB331.txt");
    public static File file4 = new File("ogapB1131.txt");
    public static File file5 = new File("ogapC333.txt");
    public static File file6 = new File("ogapC1633.txt");
    public static File file7 = new File("opmed5.txt");
    public static File file8 = new File("opmed10.txt");
    public static File file9 = new File("opmed20.txt");
    public static File file10 = new File("opmed40.txt");
    
    public static File infile1 = new File("332PM_GapA.txt");
    //public static File infile1 = new File("inaaa.txt");
    public static File infile2 = new File("1232PM_GapA.txt");
    public static File infile3 = new File("331PM_GapB.txt");
    public static File infile4 = new File("1131PM_GapB.txt");
    public static File infile5 = new File("333PM_GapC.txt");
    public static File infile6 = new File("1633PM_GapC.txt");
    public static File infile7 = new File("pmed5.txt");
    public static File infile8 = new File("pmed10.txt");
    public static File infile9 = new File("pmed20.txt");
    public static File infile10 = new File("pmed40.txt");
    
    public static File input;


    public static void main(String[] args) throws IOException
    {                       
                        
        gerador_aleatorios = new Random();                        

            p = new ArrayList<Integer>();
            pmed = new ArrayList<Integer>();                		        
              
                /******************/
                /*ENTRADA DE DADOS*/
                /******************/
            
                input = new File(args[0]);
                
                System.out.println("FOI!!");
                
                inicializa_matriz_entrada(input);
                gera_arquivo_saida(input);
                
                System.out.println("FOI!!");
                
	        stop2 = Integer.parseInt(args[1]);
	        temperatura = Double.parseDouble(args[2]);
	        resfriamento = Double.parseDouble(args[3]);; //Valor (0,1)

                System.out.println("FOI!!");
                
                /********************************************/
                /*EXECUÇÃO DA HEURÍSTICA SIMULATED ANNEALING*/
                /********************************************/
	      int res = simulated_annealing(stop2, dimension*dimension, temperatura, resfriamento);
	        	   
        
        System.out.println("Melhor custo = " + res);
        
        
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
    
    
   public static void imprime_estrutura(int[][] matriz, int custo)
    {
        System.out.println("");
        
        System.out.print("N: ");
        for(int i=0; i<dimension; i++)
        {
            System.out.print(matriz[NO][i] + "\t");
                        
        }
        System.out.println("");
        
        System.out.print("F: ");
        for(int j=0; j<dimension; j++)
        {
            System.out.print(matriz[FACILIDADE][j] + "\t");
                        
        }
        System.out.println("");
        System.out.print("C: ");
        for(int i=0; i<dimension; i++)
        {
            System.out.print(matriz[CUSTO][i] + "\t");
                        
        }
        
        System.out.println("");
        System.out.println("CUSTO: " + custo);
        System.out.println("");
    }
   
   public static void atualiza_solucao(double temperatura)
   {
        /*COPIA INFORMAÇÃO DA SOLUÇÃO ATUAL PARA MUDAR NA CANDIDATA*/
       int custo=0;
        for(int i=0; i<dimension; i++)
        {
            solucao_atual[NO][i] = solucao_candidata2[NO][i];
            solucao_atual[FACILIDADE][i] = solucao_candidata2[FACILIDADE][i];
            solucao_atual[CUSTO][i] = solucao_candidata2[CUSTO][i];
            custo+= solucao_candidata2[CUSTO][i];
        
        }   
        System.out.println("");
        System.out.println("CUSTO NOVO: " + custo + "\t" + "T: " + temperatura);
       // imprime_estrutura(solucao_candidata2, custo);

   }
    
    
    public static int perturba_solucao(int custo_anterior)
    {
        /*
        1) Seleciona uma mediana random
        2) Verifica qual é a melhor dessa mediana para todos os outros clientes
        3) Troca com o melhor dentre as opções atuais
        */
        
        Random rand = new Random();
        int facilidade_escolhida;
        
                
        //1) Seleção da meidana de forma randômica
        facilidade_escolhida = solucao_atual[NO][rand.nextInt(facility_total)]; 
        
        //System.out.println("Facilidade selecionada: " + facilidade_escolhida);
        
        int melhor_no_atual=0;
        int indice_melhor_no = 0;
        int custo_melhor_troca = VALOR_INFINITO*dimension;
        
        int custo_ligacao_antiga = 0;
        
        /*COPIA INFORMAÇÃO DA SOLUÇÃO ATUAL PARA MUDAR NA CANDIDATA*/
        for(int i=0; i<dimension; i++)
        {
            solucao_candidata2[NO][i] = solucao_atual[NO][i];
            solucao_candidata2[FACILIDADE][i] = solucao_atual[FACILIDADE][i];
            solucao_candidata2[CUSTO][i] = solucao_atual[CUSTO][i];
        }                     

        
        //2) Para todos os nós não facilidade, verificar qual é a melhor troca (menos custosa)
        //Para toda ligação da facilidade a ser trocada com seus clientes, verificar distância do NOVO cliente para esses outros clientes
        for(int i=facility_total; i< dimension; i++)
        {
           //Verifica para cada nodo se a troca é a melhor
           int custo_no_i = 0;
           for(int j=facility_total; j<dimension; j++)
           {
               if(solucao_atual[FACILIDADE][j] == facilidade_escolhida) //Se achou um ligante
                   custo_no_i+=matriz_entrada[solucao_atual[NO][i]][solucao_atual[NO][j]]; //Verifica o custo do nó-facilidade (candidato) até outro nó
           }
           custo_no_i+=matriz_entrada[solucao_atual[NO][i]][facilidade_escolhida]; //Acrescenta valor de nó candidato até ex-facilidade
           
           if(custo_no_i <= custo_melhor_troca)
           {
               melhor_no_atual = solucao_atual[NO][i];
               indice_melhor_no = i;
               custo_melhor_troca = custo_no_i;
           }
           
        }        
        
        int custo_solucao_atual = 0;
        //3) Já encontrou melhor substituição local. Então, arrumar estrutura
        for(int i= facility_total; i<dimension; i++)
        {
            if(solucao_atual[FACILIDADE][i] == facilidade_escolhida)
            {
                //Salva o custo anterior das ligações para atualizar valor no final
               // custo_ligacao_antiga+= solucao_candidata2[CUSTO][i];
                
                //Para todos que antiga facilidade ligava, melhor nó agora liga
                solucao_candidata2[FACILIDADE][i] = melhor_no_atual;
                solucao_candidata2[CUSTO][i] = matriz_entrada[melhor_no_atual][solucao_atual[NO][i]];
               // custo_solucao_atual+=     matriz_entrada[melhor_no_atual][solucao_atual[NO][i]];
                
                
            }
        }
              
        //System.out.println("Verificado");
        
        /*Troca de lugar a ex-facilidade pelo nó agora facilidade*/
        int id_antiga_facilidade = busca_id_facilidade(facilidade_escolhida);          
        
        solucao_candidata2[NO][indice_melhor_no] = facilidade_escolhida;
        solucao_candidata2[FACILIDADE][indice_melhor_no] = melhor_no_atual;
        solucao_candidata2[CUSTO][indice_melhor_no] = matriz_entrada[melhor_no_atual][facilidade_escolhida];
         //custo_solucao_atual+= matriz_entrada[melhor_no_atual][facilidade_escolhida];                    
        
        solucao_candidata2[NO][id_antiga_facilidade] = melhor_no_atual;
        solucao_candidata2[FACILIDADE][id_antiga_facilidade] = melhor_no_atual;
        solucao_candidata2[CUSTO][id_antiga_facilidade] = 0;
        
         // System.out.println("Melhor no atual: " + melhor_no_atual);
                 
        //System.out.println("NOVO CUSTO: " + custo_solucao_atual);
        
       // System.out.println("");
       // System.out.print("SOLUCAO PERTURBADA: ");
                
        custo_ligacao_antiga = 0;
        custo_solucao_atual = 0;
        for(int i=0; i<dimension; i++)
        {
            if(solucao_candidata2[FACILIDADE][i] == melhor_no_atual)
            {
                custo_solucao_atual+=solucao_candidata2[CUSTO][i];
                custo_ligacao_antiga+=solucao_atual[CUSTO][i];
            }
        }
        
        
        
        
        /*CÁLCULO DO NOVO CUSTO DA SOLUÇÃO*/
        custo_solucao_atual = custo_anterior - custo_ligacao_antiga + custo_solucao_atual;
        
        //imprime_estrutura(solucao_candidata2, custo_solucao_atual);
        
        return custo_solucao_atual;
    }
    
    public static int busca_id_facilidade_shuffle(int facilidade)
    {
        //System.out.println("Buscando por: " + facilidade);
        //System.out.print("P-MEDIANAS: ");
        
        
        for(int i=0; i<facility_total; i++)
        {
            //System.out.print(solucao_atual[0][i] + " ");
            if(solucao_candidata2[NO][i] == facilidade)
            {
             //System.out.println("ENCONTROU!!");
                return i; //retorno id
            }
        }
        
        return -1;
    }
    
    public static int shuffle()
    {
        int custo_solucao_inicial = 0;
        /*Inicializa matriz com infinito*/
        for(int i =0; i<dimension; i++)
        {
            solucao_candidata2[NO][i] = VALOR_INFINITO;
            solucao_candidata2[FACILIDADE][i] = i;
            solucao_candidata2[CUSTO][i] = VALOR_INFINITO;
        }
                      
            pmed.clear();
            pmed = seleciona_p_medianas(); //P contem a lista do id adas facilidades
            
            /*Preenche posição das medianas*/
            int ind=0;
            for (Integer facilidade : pmed) //Para cada facilidade
                {
                    solucao_candidata2[NO][ind]=facilidade; //Coloca no local das facilidades da solução atual
                    solucao_candidata2[FACILIDADE][ind]=facilidade; //A facilidade é atendida por ela mesma
                    solucao_candidata2[CUSTO][ind]=0;          //Custo de se atender a facilidade = 0
                    ind++;
                    
                    //System.out.print(facilidade + " ");
                }   
            
            /*Preenche o restante do nodos na estrutura*/
            for(int i=0; i<dimension; i++)
            {
                //Se não for uma facilidade colocar no espaço correto dos clientes!
                if(!pmed.contains(i))
                {                  
                    Iterator<Integer> itr = pmed.iterator();
                    int melhor_facilidade_posicao=-1;
                    int melhor_custo_atual = VALOR_INFINITO+1;   
                    
                   
                    /*VERIFICA PARA TODAS AS FACILIDADES QUAL A MIS PRÓXIMA!*/
                    while(itr.hasNext())
                    {
                        
                        int facilidade = itr.next();//Seleciona uma facilidade e verifica a distancia até cliente                                 

                        //System.out.println("Matriz_entrada["+facilidade+"]"+"["+cliente+"]"+ " = " + matriz_entrada[facilidade][cliente]);
                        //System.out.println("Facilidade => " + facilidade);
                        if(matriz_entrada[facilidade][i] < melhor_custo_atual)//Se encontrou menor distância que a atual, atualiza
                        { //Se for melhor atualiza                        
                            
                           //System.out.println("Facilidade sendo pesquisada: " + facilidade);
                           melhor_facilidade_posicao = busca_id_facilidade_shuffle(facilidade); 
                                                     
                           
                           melhor_custo_atual = matriz_entrada[facilidade][i];
                           
                            //System.out.println("Melhor facilidade: " + melhor_facilidade_posicao);
                           
                           if(melhor_facilidade_posicao < 0)
                               System.out.println("Erro no algoritmo!!!\n" + melhor_facilidade_posicao);
                        }                        
                    }
                    
                 
                    
                    /*AQUI JÁ VAI TER QUAL FACILIDADE É A MAIS PRÓXIMA*/
                    solucao_candidata2[NO][ind]= i;  //Salva qual id do nó
                    solucao_candidata2[FACILIDADE][ind] =  solucao_candidata2[NO][melhor_facilidade_posicao]; //Qual a facilidade mais próxima do nó
                    solucao_candidata2[CUSTO][ind] = melhor_custo_atual; //Qual o custo da facilidade ao nó                     
                    custo_solucao_inicial+=melhor_custo_atual;
                    
                 ind++;   
                }               
                
            }
        
            
           
        
        return custo_solucao_inicial;
    }
    
    public static int simulated_annealing(int stop2, int stop1, double temperatura, double resfriamento)
    {
        
        int valor_perturbacao;
        //1) Gera solução inicial e obtem custo dessa solução
        int melhor_valor_global = solucao_inicial();
        int shuffle = 0;
        /*Inicia laço do algoritmo*/
        //////////////////////
        //repeat STOP2 vezes//
        //////////////////////
        //Começa a contar o tempo
        long startTime = System.currentTimeMillis();
        
        while(System.currentTimeMillis() - startTime < stop2*60000)
        {
            
            //////////////////////
            //repeat STOP1 vezes//
            //////////////////////
            for(int j=0; j<stop1; j++)
            {
                //3) Perturba melhor solução atual      
                
                ///////////////////////////////////////////////////
                //seleciona s '∈ N (s) que ainda não foi visitado//
                ///////////////////////////////////////////////////
                //if(shuffle ==1 || melhorou == 0)
                //if(melhorou == 0)
                if(shuffle == 1)
                {
                   valor_perturbacao = shuffle();                    
                   shuffle = 0;
                }
                else
                     valor_perturbacao = perturba_solucao(melhor_valor_global);  
                
                ////////////////////////
                //if f(s') ≤ f(s) then//
                ////////////////////////
                if(valor_perturbacao <= melhor_valor_global)
                {
                    //////////
                    //s:= s'//
                    //////////
                    
                    //Se gerou, atualiza solução global atual
                    atualiza_solucao(temperatura);
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
                      atualiza_solucao(temperatura);
                      melhor_valor_global = valor_perturbacao;
                    }    
                }
            }
            //////////////
            //T := T × r//
            //////////////
            temperatura = temperatura * resfriamento;
            shuffle = 1;    
        }
        
        return melhor_valor_global;

    }
    
    public static int funcao_avaliacao(int[][] matriz)
    {
        /*
            Percorre matriz calculando o custo total
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
        
      // ArrayList <Integer> pmed = new ArrayList<Integer>();
        
        for(i=0; i<facility_total; i++)
        {
            facility = rand.nextInt(dimension);
            while(pmed.contains(facility))
            {
                facility = rand.nextInt(dimension);
            }
            
            pmed.add(facility);
        }               
        

        /*
            System.out.println(pmed.size());
            System.out.print("P-med: ");
            for (Integer item : pmed) 
            {   
                System.out.print(item + " ");
            }
        */
        
        
       
        
        return pmed;
    }
    
    /*Função retorna em qual posição do vetor solucao_atual está a facilidade */
    public static int busca_id_facilidade(int facilidade)
    {
        //System.out.println("Buscando por: " + facilidade);
        //System.out.print("P-MEDIANAS: ");
        
        
        for(int i=0; i<facility_total; i++)
        {
            //System.out.print(solucao_atual[0][i] + " ");
            if(solucao_atual[NO][i] == facilidade)
            {
             //System.out.println("ENCONTROU!!");
                return i; //retorno id
            }
        }
        
        return -1;
    }
    
    public static int solucao_inicial()
    {
        int custo_solucao_inicial = 0;
        /*Inicializa matriz com infinito*/
        for(int i =0; i<dimension; i++)
        {
            solucao_atual[NO][i] = VALOR_INFINITO;
            solucao_atual[FACILIDADE][i] = i;
            solucao_atual[CUSTO][i] = VALOR_INFINITO;
        }

            p = seleciona_p_medianas(); //P contem a lista do id adas facilidades
            
            /*Preenche posição das medianas*/
            int ind=0;
            for (Integer facilidade : p) //Para cada facilidade
                {
                    solucao_atual[NO][ind]=facilidade; //Coloca no local das facilidades da solução atual
                    solucao_atual[FACILIDADE][ind]=facilidade; //A facilidade é atendida por ela mesma
                    solucao_atual[CUSTO][ind]=0;          //Custo de se atender a facilidade = 0
                    ind++;
                }          
            
            /*Preenche o restante do nodos na estrutura*/
            for(int i=0; i<dimension; i++)
            {
                //Se não for uma facilidade colocar no espaço correto dos clientes!
                if(!p.contains(i))
                {                  
                    Iterator<Integer> itr = p.iterator();
                    int melhor_facilidade_posicao=-1;
                    int melhor_custo_atual = VALOR_INFINITO+1;   
                    
                    /*VERIFICA PARA TODAS AS FACILIDADES QUAL A MAIS PRÓXIMA!*/
                    while(itr.hasNext())
                    {
                        
                        int facilidade = itr.next();//Seleciona uma facilidade e verifica a distancia até cliente                                 

                        //System.out.println("Matriz_entrada["+facilidade+"]"+"["+cliente+"]"+ " = " + matriz_entrada[facilidade][cliente]);
                        //System.out.println("Facilidade => " + facilidade);
                        if(matriz_entrada[facilidade][i] < melhor_custo_atual)//Se encontrou menor distância que a atual, atualiza
                        { //Se for melhor atualiza                        
                            
                             //System.out.println("Facilidade sendo pesquisada: " + facilidade);
                           melhor_facilidade_posicao = busca_id_facilidade(facilidade); 
                                                     
                           
                           melhor_custo_atual = matriz_entrada[facilidade][i];
                           
                            //System.out.println("Melhor facilidade: " + melhor_facilidade_posicao);
                           
                           if(melhor_facilidade_posicao < 0)
                               System.out.println("Erro no algoritmo!!!\n" + melhor_facilidade_posicao);
                        }                        
                    }
                    
                    /*AQUI JÁ VAI TER QUAL FACILIDADE É A MAIS PRÓXIMA*/
                    solucao_atual[NO][ind]= i;  //Salva qual id do nó
                    solucao_atual[FACILIDADE][ind] = solucao_atual[NO][melhor_facilidade_posicao]; //Qual a facilidade mais próxima do nó
                    solucao_atual[CUSTO][ind] = melhor_custo_atual; //Qual o custo da facilidade ao nó                     
                    custo_solucao_inicial+=melhor_custo_atual;

                 ind++;   
                }               
                
            }

/*            
         for(int i=0; i<dimension; i++)
         {
             System.out.println("NO: " + solucao_atual[NO][i] + " , FACILIDADE: " + solucao_atual[FACILIDADE][i]+ " , CUSTO: " + solucao_atual[CUSTO][i]);
             
         }
            
            
         System.out.println("CUSTO INICIAL: " + custo_solucao_inicial);
 */
            System.out.println("");
            System.out.println("SOOLUCAO ATUAL: ");
            imprime_estrutura(solucao_atual, custo_solucao_inicial);
            
        return custo_solucao_inicial;        
             
    }
    
    public static double probabilidade_de_saltos(int valor_candidato, int valor_global, double temperatura)
    {
        //Com probabilidade e^[−(f(s')−f(s))/kT]
        //-Lembro da professora ter falado sobe o K não fazer muita diferença e por isso poderíamos ignorar
        return Math.exp(-(valor_candidato - valor_global)/temperatura);
    }

    public static int[][] liga_clientes_facilidades(int[][] melhor, int[][] entrada)
    {
        int melhor_facilidade_atual = -1;

        for(int i=0; i<dimension; i++)
        {
            int melhor_distancia_atual = VALOR_INFINITO;

            if(!p.contains(i))
            {
                melhor_facilidade_atual = -1;

                for (Integer j : p)
                {
                    if(entrada[j][i] < melhor_distancia_atual)
                    {
                        melhor_distancia_atual = entrada[j][i];

                        melhor_facilidade_atual = j;
                    }
                }
            }

            if(melhor_facilidade_atual != -1)
                melhor[melhor_facilidade_atual][i] = melhor_distancia_atual;
        }

        for (Integer facilidade : p)
            melhor[facilidade][facilidade] = 0;

        return melhor;
    }

    public static int[][] inicializa_matriz_inifinita(int[][] matriz)
    {
        int i, j;

        for(i=0; i<dimension;i++)
            for(j=0; j<dimension; j++)
                matriz[i][j] = VALOR_INFINITO;

        return matriz;
    }

    public static void printa_matriz(int[][] matriz)
    {
         for(int i=0; i<dimension; i++)
         {
            for(int j=0; j<dimension; j++)
                    System.out.print(matriz[i][j]+" ");

            System.out.print("\n");
         }
    }

    public static boolean verifica_factibilidade(int[][] matriz)
    {
        int i;

        boolean reached = false;

        for(i=0; i<dimension; i++)
        {
            if(!p.contains(i))
            {
                reached = false;

                for (Integer j : p)
                {
                    if(matriz[j][i] != VALOR_INFINITO)
                    {
                        reached = true;
                        break;
                    }
                }

                if(reached == false)
                    return false;
            }
        }

        if(reached == false)
            return false;
        else
            return true;
    }

    public static void inicializa_matriz_entrada(File file_path) throws FileNotFoundException, IOException
    {
        InputStream in = new FileInputStream(file_path);
        
        reader = new BufferedReader(new InputStreamReader(in));
        
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
        	solucao_atual = new int [3][dimension];
        	solucao_candidata2 = new int [3][dimension];
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
           	melhor_solucao = new int[dimension][dimension];
           	solucao_atual = new int [3][dimension];
           	solucao_candidata = new int[dimension][dimension];
           	solucao_candidata2 = new int [3][dimension];
           
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
                 {
                    if(TIPO_ENTRADA == 0){
                        matriz_entrada[i][j] = 0;
                    }
                    else if(TIPO_ENTRADA == 1)
                         matriz_entrada[i][j] = VALOR_INFINITO;
                    
                 }
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
    
