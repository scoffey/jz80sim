		aseg
		org 3000h


;en zona se ingresa un valor de 16 bits pudiendo ser este un rótulo, una constante o un registro de 16 bits
;reg puede ser cualquier registro a,b,c,d,h,l
descarga	macro zona, reg
		ld (backup), a
		ld a, reg
		ld (zona), a
		ld a, (backup)
		jr backup + 1
backup		ds 1
		endm



;reg = v - {a}
;zona = cualquier valor, registro o rotulo de 16 bits
carga		macro reg, zona
		ld (backup), a
		ld a, (zona)
		ld reg, a
		ld a, (backup)
		jr backup +1
backup		ds 1
		endm
		

comienzo
		nop
		carga b, dato
		inc b
		descarga rta1, b
		inc b
		descarga rta2, b
		rst 38h

dato		db 5
rta1		ds 1
rta2		ds 1
		end comienzo
