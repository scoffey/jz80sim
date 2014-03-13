		aseg
		org 100h
;dado un rotulo o registro de 16 bits toma el valor que esta en la posicion de memoria indicada y si es una letra lo pasa a mayusculas
aMayuscula	macro rotulo_letra
		local backup, no_letra
		ld (backup), a
		ld a, (rotulo_letra)
		cp 'x'+1
		jp p, no_letra
		cp 'a'
		jp n, no_letra
		add a, 'a'- 'A'
		ld (rotulo_letra), a
no_letra	ld a, backup
		jr backup + 1
backup		ds 1
		endm
inicio		aMayusluca ix
		rst 38h
		end inicio
