; Ejercicio 7 del TP 2.


		aseg
		org	1000h
start:		ld	A, 13h		; carga registro A con 13h                
		add	A, 20h		; se suma 20h
		add	A, 0CFh		; se suma CFh
		rst  38h			           
		end  start



