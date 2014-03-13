; Ejercicio 12 del TP 12.
; Incrementea el contenido de la posicion 1900h  
; hasta que se active el flag de carry


		aseg
		org	1850h
start:	
		
		ld	A, (1900h)
sigue:		inc	A
		jp	NC,sigue

		rst  	38h		          
		end  	start



