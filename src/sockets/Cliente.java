package sockets;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.net.*;

public class Cliente {

	public static void main(String[] args) {
			// TODO Auto-generated method stub
			
			MarcoCliente mimarco=new MarcoCliente();
			
			mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}

	}


	class MarcoCliente extends JFrame{
		
		

		public MarcoCliente(){
			
			setBounds(600,300,280,350);
					
			LaminaMarcoCliente milamina=new LaminaMarcoCliente();
			
			add(milamina);
			
			setVisible(true);
			}	
		
	}

	class LaminaMarcoCliente extends JPanel{
		

		public LaminaMarcoCliente(){
			
			nick = new JTextField(5);
			
			add(nick);
		
			JLabel texto=new JLabel(" * CHAT *");
			
			add(texto);
			
			ip = new JTextField(8);
						
			add(ip);
			
			campoChat = new JTextArea(12,20);
			
			add(campoChat);
		
			campo1=new JTextField(20);
		
			add(campo1);		
		
			miboton=new JButton("Enviar");
			
			EnviaText mievento = new EnviaText();
			miboton.addActionListener(mievento);
			
			add(miboton);	
			
		}
		
		
		private class EnviaText implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//System.out.println(campo1.getText());
				
				try {
					Socket misocket = new Socket("localhost", 9999);   //los argumentos que necesita son dirección de red(ip) y puerto
					
					
					PaqueteEnvio datos = new PaqueteEnvio();
					
					/*
					 * Empaquetamos lo que queremos enviar al destinatario dentro de la variable datos
					 */
					datos.setNick(nick.getText());
					datos.setIp(ip.getText());
					datos.setMensaje(campo1.getText()); //mensaje
					
					/*
					 * Ahora tenemos que enviar el objeto datos al servidor a través de la red
					 */
					
					ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream()); // Creamos un flujo de salida de objetos con ObjectOutputStream
					
					paquete_datos.writeObject(datos);
					
					misocket.close();
					
					
					
			//		DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream()); //se le indica por dónde van a circular los datos
					
			//		flujo_salida.writeUTF(campo1.getText()); //recupera el texto que hay en el JTextField
					
					/*
					 * En este punto el programa recupera el texto que hemos escrito y los manda usando DataOutputStream al Socket con los argumentos que le hayamos indicado
					 */
					
			//		flujo_salida.close(); //cerramos el flujo de datos
					
					
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					System.out.println("Error en la conexión con el servidor: " +e1.getMessage());
				}	
			}
			
		}
			
			
			
		private JTextField campo1;
		private JTextField nick;
		private JTextField ip;

		
		private JTextArea campoChat;
		
		private JButton miboton;
		
	}
	
	/*
	 * Esta clase debe estar serializada para que al enviar el objeto este se convierta en 0 y 1 y se envíe por la red
	 */
	class PaqueteEnvio implements Serializable{
		
		private String nick;
		private String ip;
		private String mensaje;
		
		
		
		
		//Getters y Setters
		public String getNick() {
			return nick;
		}
		public void setNick(String nick) {
			this.nick = nick;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getMensaje() {
			return mensaje;
		}
		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}
		
		

	}


